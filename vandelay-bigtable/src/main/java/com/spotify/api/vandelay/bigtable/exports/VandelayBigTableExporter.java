/*-
 * -\-\-
 * vandelay-api
 * --
 * Copyright (C) 2022 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.api.vandelay.bigtable.exports;

import com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.Cluster;
import com.google.cloud.bigtable.admin.v2.models.Instance;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.spotify.api.vandelay.bigtable.client.VandelayBigTableClient;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableExportConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration.Builder;
import com.spotify.api.vandelay.bigtable.event.VandelayBigTableDeleteInstanceEvent;
import com.spotify.api.vandelay.bigtable.event.VandelayBigTableExportEvent;
import com.spotify.api.vandelay.bigtable.event.VandelayBigTableRowsExportFinishedEvent;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableAppProfileMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableBackupMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableClusterMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableInstanceMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableTableMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.iam.IAMPolicyMapperFunction;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableCell;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRow;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableAppProfile;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableBackup;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableCluster;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableTable;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.core.event.VandelayEventListener;
import com.spotify.api.vandelay.core.event.VandelayEventPublisher;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import com.spotify.api.vandelay.core.type.Status;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VandelayBigTableExporter
    implements VandelayBigTableExport, VandelayEventPublisher<VandelayBigTableExportEvent> {

  private static final Logger logger = LoggerFactory.getLogger(VandelayBigTableExporter.class);

  private final VandelayBigTableClient vandelayBigTableClient;
  private final List<VandelayEventListener<VandelayBigTableExportEvent>> observers =
      new ArrayList<>();

  public VandelayBigTableExporter(final VandelayBigTableClient bigTableClient) {
    this.vandelayBigTableClient = bigTableClient;
  }

  @Override
  public Status exportTo(final VandelayBigTableExportConfiguration exportConfiguration) {

    try {
      final String instanceId = exportConfiguration.getInstanceId();
      final String projectId = exportConfiguration.getProjectId();
      logger.info("Attempting export for project id " + projectId + " instance id " + instanceId);

      logger.info("Getting clients for instance");
      final BigtableInstanceAdminClient instanceAdminClient =
          vandelayBigTableClient.getInstanceAdminClient(exportConfiguration);
      final BigtableTableAdminClient tableAdminClient =
          vandelayBigTableClient.getTableAdminClient(exportConfiguration);
      final BigtableDataClient dataClient =
          vandelayBigTableClient.getDataClient(exportConfiguration);
      logger.info("All clients retrieved for instance");

      final VandelayBigTableExportEvent instanceInformation =
          getInstanceInformation(
              instanceId, exportConfiguration, instanceAdminClient, tableAdminClient);
      logger.info("Notifying listeners of instance information event");
      notifyListeners(instanceInformation);

      logger.info("Beginning export for instance: " + instanceId);

      final Consumer<String> performExport =
          (final String tableName) ->
              dataClient
                  .readRows(Query.create(tableName))
                  .forEach(
                      row -> {
                        final List<VandelayBigTableCell> cells =
                            row.getCells().stream()
                                .map(
                                    x ->
                                        new VandelayBigTableCell(
                                            x.getFamily(),
                                            new Base64EncodedValue(
                                                "qualifier", x.getQualifier().toByteArray()),
                                            x.getTimestamp(),
                                            new Base64EncodedValue(
                                                "value", x.getValue().toByteArray()),
                                            x.getLabels()))
                                .toList();
                        notifyListeners(
                            new VandelayBigTableExportEvent(
                                new VandelayBigTableRow(
                                    tableName,
                                    row.getKey().toString(StandardCharsets.UTF_8),
                                    cells)));
                      });
      if (exportConfiguration.getParallelExport()) {
        logger.info("Beginning parallel export of rows");
        tableAdminClient.listTables().stream().parallel().forEach(performExport);
      } else {
        logger.info("Beginning export of rows");
        tableAdminClient.listTables().forEach(performExport);
      }

      logger.info("Finished exporting rows for instance: " + instanceId);
      notifyListeners(
          new VandelayBigTableExportEvent(new VandelayBigTableRowsExportFinishedEvent(instanceId)));

      logger.info("Should delete instance on export? " + exportConfiguration.getDeleteOnExport());
      if (exportConfiguration.getDeleteOnExport()) {
        deleteInstance(instanceId, instanceAdminClient, tableAdminClient);
        notifyListeners(
            new VandelayBigTableExportEvent(
                new VandelayBigTableDeleteInstanceEvent(
                    exportConfiguration.getProjectId(), instanceId)));
      }

      return Status.success();
    } catch (IOException ex) {
      logger.error("IOException occurred exporting rows", ex);
      return Status.fail(ex.getMessage(), ex);
    }
  }

  private VandelayBigTableExportEvent getInstanceInformation(
      final String instanceId,
      final VandelayBigTableExportConfiguration exportConfiguration,
      final BigtableInstanceAdminClient instanceAdminClient,
      final BigtableTableAdminClient tableAdminClient) {

    final VandelayBigTableGCPConfiguration.Builder builder =
        new VandelayBigTableGCPConfiguration.Builder(
            exportConfiguration.getProjectId(), instanceId);

    getBigTableInstanceConfiguration(instanceId, instanceAdminClient, builder);
    getBigTableBackupConfiguration(instanceId, instanceAdminClient, tableAdminClient, builder);
    getBigTableTableInformation(tableAdminClient, builder);

    return new VandelayBigTableExportEvent(builder.build());
  }

  private void getBigTableInstanceConfiguration(
      final String instanceId,
      final BigtableInstanceAdminClient instanceAdminClient,
      final Builder builder) {

    final Instance instance = instanceAdminClient.getInstance(instanceId);
    builder.setInstance(new BigTableInstanceMapperFunction().convertTo(instance).orElse(null));

    final List<BigTableCluster> clusters =
        instanceAdminClient.listClusters(instance.getId()).stream()
            .map(x -> new BigTableClusterMapperFunction().convertTo(x))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    builder.setClusters(clusters);
    logger.info("Set " + clusters.size() + " clusters");

    final List<BigTableAppProfile> appProfiles =
        instanceAdminClient.listAppProfiles(instance.getId()).stream()
            .map(x -> new BigTableAppProfileMapperFunction().convertTo(x))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    builder.setInstanceAppProfiles(appProfiles);
    logger.info("Set " + appProfiles.size() + " app profiles");

    builder.setInstancePolicies(
        new IAMPolicyMapperFunction()
            .convertTo(instanceAdminClient.getIamPolicy(instance.getId()))
            .orElse(null));
  }

  private void getBigTableTableInformation(
      final BigtableTableAdminClient tableAdminClient, final Builder builder) {

    final List<String> tables = tableAdminClient.listTables();
    if (!tables.isEmpty()) {

      final Map<String, BigTableTable> tableTables = new HashMap<>();
      final Map<String, IAMPolicy> tablePolicies = new HashMap<>();
      for (final var tableId : tables) {
        tableTables.put(
            tableId,
            new BigTableTableMapperFunction()
                .convertTo(tableAdminClient.getTable(tableId))
                .orElse(null));
        tablePolicies.put(
            tableId,
            new IAMPolicyMapperFunction()
                .convertTo(tableAdminClient.getIamPolicy(tableId))
                .orElse(null));
      }

      tableTables.values().removeIf(Objects::isNull);
      tablePolicies.values().removeIf(Objects::isNull);

      builder.setTables(tableTables);
      logger.info("Set " + tableTables.size() + " tables");

      builder.setTablePolicies(tablePolicies);
      logger.info("Set " + tablePolicies.size() + " table policies");
    } else {
      logger.info("No tables found");
    }
  }

  private void getBigTableBackupConfiguration(
      final String instanceId,
      final BigtableInstanceAdminClient instanceAdminClient,
      final BigtableTableAdminClient tableAdminClient,
      final Builder builder) {

    final List<Cluster> instanceClusters = instanceAdminClient.listClusters(instanceId);
    if (!instanceClusters.isEmpty()) {
      final Map<String, List<BigTableBackup>> tableBackupsMap = new HashMap<>();
      final Map<String, Map<String, List<IAMPolicy>>> tablePoliciesMap = new HashMap<>();
      for (final var cluster : instanceClusters) {
        final List<BigTableBackup> tableBackups = new ArrayList<>();
        final Map<String, List<IAMPolicy>> tablePolicies = new HashMap<>();
        for (final var backupId : tableAdminClient.listBackups(cluster.getId())) {
          tableBackups.add(
              new BigTableBackupMapperFunction()
                  .convertTo(tableAdminClient.getBackup(cluster.getId(), backupId))
                  .orElse(null));
          final List<IAMPolicy> policies = tablePolicies.getOrDefault(backupId, new ArrayList<>());
          policies.add(
              new IAMPolicyMapperFunction()
                  .convertTo(tableAdminClient.getBackupIamPolicy(cluster.getId(), backupId))
                  .orElse(null));
          tablePolicies.put(backupId, policies);
        }
        tableBackupsMap.put(
            cluster.getId(), tableBackups.stream().filter(Objects::nonNull).toList());
        tablePolicies.values().removeIf(Objects::isNull);
        tablePoliciesMap.put(cluster.getId(), tablePolicies);
      }

      builder.setTableBackup(tableBackupsMap);
      logger.info("Set " + tableBackupsMap.size() + " table backups");

      builder.setTableBackupPolicies(tablePoliciesMap);
      logger.info("Set " + tablePoliciesMap.size() + " table backup policies");
    } else {
      logger.info("No clusters found");
    }
  }

  private void deleteInstance(
      final String instanceId,
      final BigtableInstanceAdminClient instanceAdminClient,
      final BigtableTableAdminClient tableAdminClient) {

    logger.info("Deleting clusters for instance id " + instanceId);
    instanceAdminClient.listClusters(instanceId).stream()
        .map(Cluster::getId)
        .forEach(
            clusterId ->
                tableAdminClient
                    .listBackups(clusterId)
                    .forEach(backupId -> tableAdminClient.deleteBackup(clusterId, backupId)));

    logger.info("Deleting tables for instance id " + instanceId);
    tableAdminClient.listTables().forEach(tableAdminClient::deleteTable);

    logger.info("Deleting instance id " + instanceId);
    instanceAdminClient.deleteInstance(instanceId);
  }

  @Override
  public void addListener(final VandelayEventListener<VandelayBigTableExportEvent> observer) {
    logger.trace("Adding listeners for export events");
    observers.add(observer);
  }

  @Override
  public void removeListener(final VandelayEventListener<VandelayBigTableExportEvent> observer) {
    logger.trace("Removing listener for export events");
    observers.remove(observer);
  }

  @Override
  public void notifyListeners(final VandelayBigTableExportEvent event) {
    logger.trace("Number of listeners being notified: " + observers.size());
    for (final var observer : observers) {
      observer.onEvent(event);
    }
  }
}
