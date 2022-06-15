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

package com.spotify.api.vandelay.bigtable.imports;

import com.google.cloud.Policy;
import com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.AppProfile;
import com.google.cloud.bigtable.admin.v2.models.Backup;
import com.google.cloud.bigtable.admin.v2.models.Cluster;
import com.google.cloud.bigtable.admin.v2.models.CreateAppProfileRequest;
import com.google.cloud.bigtable.admin.v2.models.CreateBackupRequest;
import com.google.cloud.bigtable.admin.v2.models.CreateInstanceRequest;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.GCRules.GCRule;
import com.google.cloud.bigtable.admin.v2.models.Instance;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.BulkMutation;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.protobuf.ByteString;
import com.spotify.api.vandelay.bigtable.client.VandelayBigTableClient;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableImportConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableInstanceConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableTableConfiguration;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableAppProfileMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableBackupMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableClusterMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableGCRuleMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.bigtable.BigTableInstanceMapperFunction;
import com.spotify.api.vandelay.bigtable.mappers.iam.IAMPolicyMapperFunction;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRow;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableAppProfile;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableCluster;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableInstance;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.core.type.Status;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VandelayBigTableImporter implements VandelayBigTableImport {

  private static final Logger logger = LoggerFactory.getLogger(VandelayBigTableImporter.class);

  private final VandelayBigTableClient vandelayBigTableClient;

  public VandelayBigTableImporter(final VandelayBigTableClient bigTableClient) {
    this.vandelayBigTableClient = bigTableClient;
  }

  @Override
  public Status importFrom(final VandelayBigTableImportConfiguration importConfiguration) {

    try {
      final String instanceId = importConfiguration.getInstanceId();
      final String projectId = importConfiguration.getProjectId();
      logger.info("Attempting export for project id " + projectId + " instance id " + instanceId);

      logger.info("Getting instance admin client");
      final BigtableInstanceAdminClient instanceAdminClient =
          vandelayBigTableClient.getInstanceAdminClient(importConfiguration);
      if (importConfiguration.getCreateInstanceIfDoesntExist()) {
        logger.info("Attempting to create " + instanceId + " if it doesn't exist");
        createInstance(instanceId, importConfiguration, instanceAdminClient);
      }

      logger.info("Getting table admin client");
      final BigtableTableAdminClient tableAdminClient =
          vandelayBigTableClient.getTableAdminClient(importConfiguration);
      if (importConfiguration.getCreateTableIfDoesntExist()) {
        logger.info("Attempting to create tables for " + instanceId + " if they don't exist");
        createTables(importConfiguration, tableAdminClient);
      }

      logger.info("Instance " + instanceId + " has been set up for importing");
      return Status.success();
    } catch (IOException ex) {
      logger.error("IOException occurred creating instance and tables", ex);
      return Status.fail(ex.getMessage(), ex);
    }
  }

  @Override
  public Status addRows(
      final VandelayBigTableImportConfiguration importConfiguration,
      final List<VandelayBigTableRow> rows) {
    try {
      logger.info("Adding " + rows.size() + " rows");

      logger.info("Getting data client");
      final BigtableDataClient dataClient =
          vandelayBigTableClient.getDataClient(importConfiguration);
      final Map<String, List<VandelayBigTableRow>> tableToRows =
          rows.stream()
              .collect(
                  Collectors.groupingBy(
                      VandelayBigTableRow::getTable, Collectors.toCollection(ArrayList::new)));
      logger.info("Split rows into " + tableToRows.size() + " entries");

      for (final var tableToRow : tableToRows.entrySet()) {
        final BulkMutation bulkMutation = BulkMutation.create(tableToRow.getKey());
        for (final var tableRows : tableToRow.getValue()) {
          for (final var cell : tableRows.getRowCells()) {
            bulkMutation.add(
                tableRows.getRowKey(),
                Mutation.create()
                    .setCell(
                        cell.getColumnFamily(),
                        ByteString.copyFrom(cell.getQualifier().toDecodedBytes()),
                        cell.getTimestamp(),
                        ByteString.copyFrom(cell.getValue().toDecodedBytes())));
          }
        }

        logger.info("Performing row mutation on table " + tableToRow.getKey());
        dataClient.bulkMutateRows(bulkMutation);
      }

      return Status.success();
    } catch (IOException ex) {
      logger.error("IOException occurred creating rows");
      return Status.fail(ex.getMessage(), ex);
    }
  }

  private void createInstance(
      final String instanceId,
      final VandelayBigTableImportConfiguration importConfiguration,
      final BigtableInstanceAdminClient instanceAdminClient) {
    if (instanceAdminClient.exists(instanceId)) {
      logger.debug("Instance " + instanceId + " already exists");
      return;
    }

    final VandelayBigTableGCPConfiguration gcpConfiguration =
        importConfiguration.getGcpConfiguration();
    final VandelayBigTableInstanceConfiguration instanceConfiguration =
        gcpConfiguration.getInstanceConfiguration();
    final BigTableInstance configInstance = instanceConfiguration.getInstance();
    final Optional<Instance> instance =
        new BigTableInstanceMapperFunction().convertFrom(configInstance);
    if (instance.isPresent()) {
      logger.info("Creating create instance request for instance id " + instanceId);
      final CreateInstanceRequest createInstanceRequest =
          CreateInstanceRequest.of(instanceId)
              .setDisplayName(instance.get().getDisplayName())
              .setType(instance.get().getType());
      instance.get().getLabels().forEach(createInstanceRequest::addLabel);

      final List<BigTableCluster> clusters = instanceConfiguration.getClusters();
      logger.trace("Adding " + clusters.size() + " clusters to create instance request");
      for (final var cluster : clusters) {
        Optional<Cluster> savedCluster = new BigTableClusterMapperFunction().convertFrom(cluster);
        savedCluster.ifPresent(
            value ->
                createInstanceRequest.addCluster(
                    value.getId(), value.getZone(), value.getServeNodes(), value.getStorageType()));
      }

      logger.info("Performing create instance call");
      final Instance newInstance = instanceAdminClient.createInstance(createInstanceRequest);

      final List<BigTableAppProfile> appProfiles = instanceConfiguration.getAppProfiles();
      logger.info("Found " + appProfiles.size() + " app profiles for instance");
      for (final var appProfile : appProfiles) {
        final Optional<AppProfile> savedAppProfile =
            new BigTableAppProfileMapperFunction().convertFrom(appProfile);
        if (savedAppProfile.isPresent()) {
          if (Objects.isNull(
              instanceAdminClient.getAppProfile(
                  savedAppProfile.get().getInstanceId(), savedAppProfile.get().getId()))) {
            logger.info(
                "App profile " + savedAppProfile.get().getId() + " is not set, attempting to set");
            final CreateAppProfileRequest createAppProfileRequest =
                CreateAppProfileRequest.of(
                        savedAppProfile.get().getInstanceId(), savedAppProfile.get().getId())
                    .setDescription(savedAppProfile.get().getDescription())
                    .setRoutingPolicy(savedAppProfile.get().getPolicy());

            logger.info("Performing create app profile call");
            instanceAdminClient.createAppProfile(createAppProfileRequest);
          }
        }
      }

      final IAMPolicy savedPolicy = instanceConfiguration.getAppPolicy();
      final IAMPolicy changedPolicy =
          new IAMPolicy(null, savedPolicy.getVersion(), savedPolicy.getBindingList());
      final Optional<Policy> iamPolicy = new IAMPolicyMapperFunction().convertFrom(changedPolicy);
      if (iamPolicy.isPresent()) {
        if (!iamPolicy.get().equals(instanceAdminClient.getIamPolicy(newInstance.getId()))) {
          logger.info(
              "IAM policy on the instance does not match the expected one - setting IAM policy");
          instanceAdminClient.setIamPolicy(newInstance.getId(), iamPolicy.get());
        }
      }
    }
  }

  private void createTables(
      final VandelayBigTableImportConfiguration importConfiguration,
      final BigtableTableAdminClient tableAdminClient) {
    final VandelayBigTableGCPConfiguration gcpConfiguration =
        importConfiguration.getGcpConfiguration();
    final VandelayBigTableTableConfiguration tableConfiguration =
        gcpConfiguration.getTableConfiguration();
    for (final var table : tableConfiguration.getTables().entrySet()) {
      if (tableAdminClient.exists(table.getKey())) {
        logger.info("Table " + table.getKey() + " already exists");
        continue;
      }

      logger.info("Creating table request for table " + table.getKey());
      final CreateTableRequest createTableRequest = CreateTableRequest.of(table.getKey());
      for (final var columnFamily : table.getValue().getColumnFamilies()) {
        final Optional<GCRule> gcRule =
            new BigTableGCRuleMapperFunction().convertFrom(columnFamily.getGcRule());
        if (gcRule.isPresent()) {
          createTableRequest.addFamily(columnFamily.getId(), gcRule.get());
        } else {
          createTableRequest.addFamily(columnFamily.getId());
        }
      }

      logger.info("Performing create table call");
      tableAdminClient.createTable(createTableRequest);
      if (tableConfiguration.getTablePolicies().containsKey(table.getKey())) {
        logger.info("Table " + table.getKey() + " has table IAM policies");
        final Optional<Policy> iamPolicy =
            new IAMPolicyMapperFunction()
                .convertFrom(tableConfiguration.getTablePolicies().get(table.getKey()));
        logger.info("Setting IAM policy on table " + table.getKey());
        iamPolicy.ifPresent(policy -> tableAdminClient.setIamPolicy(table.getKey(), policy));
      }
    }

    for (final var tableBackups : tableConfiguration.getTableBackups().entrySet()) {
      for (final var backup : tableBackups.getValue()) {
        final Optional<Backup> savedBackup = new BigTableBackupMapperFunction().convertFrom(backup);
        if (savedBackup.isPresent()) {
          logger.info(
              "Creating backup of cluster "
                  + tableBackups.getKey()
                  + " with backup id "
                  + backup.getId());
          logger.info("Backup source table id is " + savedBackup.get().getSourceTableId());
          final CreateBackupRequest createBackupRequest =
              CreateBackupRequest.of(tableBackups.getKey(), backup.getId())
                  .setExpireTime(savedBackup.get().getExpireTime())
                  .setSourceTableId(savedBackup.get().getSourceTableId());
          tableAdminClient.createBackup(createBackupRequest);
        }
      }
    }

    for (final var tableBackupPolicies : tableConfiguration.getTableBackupPolicies().entrySet()) {
      for (final var backupPolicies : tableBackupPolicies.getValue().entrySet()) {
        for (final var backupPolicy : backupPolicies.getValue()) {
          final Optional<Policy> iamPolicy =
              new IAMPolicyMapperFunction().convertFrom(backupPolicy);
          logger.info(
              "Setting IAM policy on backup cluster id " + tableBackupPolicies.getKey(),
              backupPolicies.getKey());
          iamPolicy.ifPresent(
              policy ->
                  tableAdminClient.setBackupIamPolicy(
                      tableBackupPolicies.getKey(), backupPolicies.getKey(), policy));
        }
      }
    }
  }
}
