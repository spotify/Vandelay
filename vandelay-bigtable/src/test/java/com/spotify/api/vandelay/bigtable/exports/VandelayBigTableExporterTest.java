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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import com.google.cloud.bigtable.admin.v2.BigtableInstanceAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.admin.v2.models.GCRules;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.spotify.api.vandelay.bigtable.client.VandelayBigTableClient;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableExportConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRowBatch;
import com.spotify.api.vandelay.core.type.Status;
import com.spotify.api.vandelay.serialization.jackson.VandelayJacksonSerializer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.threeten.bp.Duration;

/*
 * This is an integration test for export functionality. Since the internals
 * of the various BigTable admin clients cannot be mocked, this will execute
 * against an actual GCP project if enabled.
 *
 * Remove the @Disabled annotation below and provide a valid project and instance
 * id that contains a BigTable instance to export.
 */
@Disabled
public class VandelayBigTableExporterTest {

  private static final String projectId = "test-project-id";
  private static final String instanceId = "test-instance-id";

  private static BigtableInstanceAdminClient instanceAdminClient;
  private static BigtableTableAdminClient tableAdminClient;
  private static BigtableDataClient dataClient;

  @BeforeAll
  private static void setUp() throws Exception {

    instanceAdminClient = BigtableInstanceAdminClient.create(projectId);

    final BigtableTableAdminSettings.Builder tableAdminSettings =
        BigtableTableAdminSettings.newBuilder().setProjectId(projectId).setInstanceId(instanceId);
    tableAdminClient = BigtableTableAdminClient.create(tableAdminSettings.build());

    if (!tableAdminClient.exists("example-table1")) {
      tableAdminClient.createTable(
          CreateTableRequest.of("example-table1").addFamily("cf", GCRules.GCRULES.maxVersions(1)));
    }
    if (!tableAdminClient.exists("example-table2")) {
      tableAdminClient.createTable(
          CreateTableRequest.of("example-table2")
              .addFamily("cf", GCRules.GCRULES.maxAge(Duration.ofHours(1))));
    }

    final BigtableDataSettings.Builder dataSettings =
        BigtableDataSettings.newBuilder().setProjectId(projectId).setInstanceId(instanceId);

    dataClient = BigtableDataClient.create(dataSettings.build());

    dataClient.mutateRow(
        RowMutation.create("example-table1", "example-key1").setCell("cf", "col", "foo1"));
    dataClient.mutateRow(
        RowMutation.create("example-table2", "example-key2").setCell("cf", "col", "foo2"));
  }

  private void writeToFile(final String writePath, final String content) {
    try {
      final BufferedWriter writer = new BufferedWriter(new FileWriter(writePath));
      writer.write(content);
      writer.close();
    } catch (IOException ignored) {
    }
  }

  @Test
  public void testExport() throws Exception {
    final VandelayBigTableExportConfiguration configuration =
        new VandelayBigTableExportConfiguration(projectId, instanceId, false, false);

    final VandelayBigTableClient dummyClient = new VandelayBigTableClient();
    final VandelayBigTableClient spyClient = Mockito.spy(dummyClient);

    doReturn(instanceAdminClient).when(spyClient).getInstanceAdminClient(configuration);
    doReturn(tableAdminClient).when(spyClient).getTableAdminClient(configuration);
    doReturn(dataClient).when(spyClient).getDataClient(configuration);

    final VandelayBigTableExporter exporter = new VandelayBigTableExporter(spyClient);
    final VandelayBigTableRowBatch rows = new VandelayBigTableRowBatch();
    exporter.addListener(
        event -> {
          if (event.getInfoEvent().isPresent()) {
            final VandelayJacksonSerializer<VandelayBigTableGCPConfiguration> serializer =
                new VandelayJacksonSerializer<>();
            final String config = serializer.serialize(event.getInfoEvent().get().getValue());
            writeToFile("target/config.json", config);
          } else if (event.getRowExportEvent().isPresent()) {
            rows.addRow(event.getRowExportEvent().get().getValue());
          }
        });

    final Status exportStatus = exporter.exportTo(configuration);

    final VandelayJacksonSerializer<VandelayBigTableRowBatch> serializer =
        new VandelayJacksonSerializer<>();
    final String data = serializer.serialize(rows);
    writeToFile("target/data.json", data);

    assertTrue(exportStatus.succeeded());
  }
}
