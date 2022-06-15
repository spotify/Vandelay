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

package com.spotify.api.vandelay.bigtable.demo;

import com.spotify.api.vandelay.bigtable.client.VandelayBigTableClient;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableExportConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableImportConfiguration;
import com.spotify.api.vandelay.bigtable.exports.VandelayBigTableExporter;
import com.spotify.api.vandelay.bigtable.imports.VandelayBigTableImporter;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRowBatch;
import com.spotify.api.vandelay.core.type.Status;
import com.spotify.api.vandelay.serialization.jackson.VandelayJacksonSerializer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BigTableDemo {

  private BigTableDemo() {}

  public static void main(final String[] args) {

    final int expectedArgCount = 5;
    if (args.length != expectedArgCount) {
      System.err.println(
          "Invalid number of arguments\n"
              + "Usage:\n"
              + "\t<jar path> export project-id instance-id output-config-path output-data-path\n"
              + "\t<jar path> import project-id instance-id input-config-path input-data-path\n");
      return;
    }

    final int indexType = 0;
    final int indexProjectId = 1;
    final int indexInstanceId = 2;
    final int indexConfigPath = 3;
    final int indexDataPath = 4;

    final String type = args[indexType];
    if ("export".equalsIgnoreCase(type)) {
      performExport(
          args[indexProjectId], args[indexInstanceId], args[indexConfigPath], args[indexDataPath]);
    } else if ("import".equalsIgnoreCase(type)) {
      performImport(
          args[indexProjectId], args[indexInstanceId], args[indexConfigPath], args[indexDataPath]);
    }
  }

  private static void performExport(
      final String projectId,
      final String instanceId,
      final String configOutputPath,
      final String dataOutputPath) {
    final VandelayBigTableExportConfiguration configuration =
        new VandelayBigTableExportConfiguration(projectId, instanceId, false, false);

    final VandelayBigTableClient client = new VandelayBigTableClient();

    final VandelayBigTableExporter exporter = new VandelayBigTableExporter(client);
    final VandelayBigTableRowBatch rows = new VandelayBigTableRowBatch();
    exporter.addListener(
        event -> {
          if (event.getInfoEvent().isPresent()) {
            System.out.println("Retrieved instance information for " + instanceId);
            final VandelayJacksonSerializer<VandelayBigTableGCPConfiguration> serializer =
                new VandelayJacksonSerializer<>();
            final String config = serializer.serialize(event.getInfoEvent().get().getValue());
            writeToFile(configOutputPath, config);
            System.out.println("Instance configuration written to " + configOutputPath);
          } else if (event.getRowExportEvent().isPresent()) {
            rows.addRow(event.getRowExportEvent().get().getValue());
          } else if (event.getExportFinishedEvent().isPresent()) {
            System.out.println("Export finished for " + instanceId);
          } else if (event.getDeleteEvent().isPresent()) {
            System.out.println("Deleted instance " + instanceId);
          }
        });

    final Status exportStatus = exporter.exportTo(configuration);
    if (exportStatus.succeeded()) {
      System.out.println("Export process succeeded");
    } else {
      System.err.println("Export process encountered an error: " + exportStatus.getMessage());
      return;
    }

    System.out.println("Writing row data to " + dataOutputPath);
    final VandelayJacksonSerializer<VandelayBigTableRowBatch> serializer =
        new VandelayJacksonSerializer<>();
    final String data = serializer.serialize(rows);
    writeToFile(dataOutputPath, data);
    System.out.println("Data written to " + dataOutputPath);
  }

  private static void performImport(
      final String projectId,
      final String instanceId,
      final String configInputPath,
      final String dataInputPath) {

    try {
      final VandelayJacksonSerializer<VandelayBigTableGCPConfiguration> configSerializer =
          new VandelayJacksonSerializer<>();
      final String configContent = Files.readString(Path.of(configInputPath));
      final VandelayBigTableGCPConfiguration config =
          configSerializer.deserialize(configContent, VandelayBigTableGCPConfiguration.class);

      final VandelayJacksonSerializer<VandelayBigTableRowBatch> dataSerializer =
          new VandelayJacksonSerializer<>();
      final String dataContent = Files.readString(Path.of(dataInputPath));
      final VandelayBigTableRowBatch rowBatch =
          dataSerializer.deserialize(dataContent, VandelayBigTableRowBatch.class);

      final VandelayBigTableImportConfiguration importConfiguration =
          new VandelayBigTableImportConfiguration(projectId, instanceId, config, true, true);

      final VandelayBigTableClient client = new VandelayBigTableClient();

      System.out.println(
          "Creating instance "
              + importConfiguration.getInstanceId()
              + " in project "
              + importConfiguration.getProjectId());
      final VandelayBigTableImporter importer = new VandelayBigTableImporter(client);
      Status status = importer.importFrom(importConfiguration);
      if (status.succeeded()) {
        System.out.println(
            "Instance " + importConfiguration.getInstanceId() + " was created successfully");
      } else {
        System.err.println("Creating the instance encountered an error: " + status.getMessage());
        return;
      }

      System.out.println("Importing " + rowBatch.getRows().size() + " rows");
      status = importer.addRows(importConfiguration, rowBatch.getRows());
      if (status.succeeded()) {
        System.out.println("Row import was successful");
      } else {
        System.err.println("Row import encountered an error: " + status.getMessage());
      }
    } catch (IOException ex) {
      System.err.println("IOException during import: " + ex.getMessage());
    }
  }

  private static void writeToFile(final String writePath, final String content) {
    try {
      final BufferedWriter writer = new BufferedWriter(new FileWriter(writePath));
      writer.write(content);
      writer.close();
    } catch (IOException ex) {
      System.err.println("Error encountered writing row data: " + ex.getMessage());
    }
  }
}
