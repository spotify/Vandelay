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

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spotify.api.vandelay.bigtable.client.VandelayBigTableClient;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableImportConfiguration;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRowBatch;
import com.spotify.api.vandelay.core.type.Status;
import com.spotify.api.vandelay.serialization.jackson.VandelayJacksonSerializer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/*
 * This is an integration test for import functionality. Since the internals
 * of the various BigTable admin clients cannot be mocked, this will execute
 * against an actual GCP project if enabled.
 *
 * The config.json and data.json files should contain the exported configuration
 * and data content, respectively. These can be obtained by running the export
 * integration test.
 *
 * Remove the @Disabled annotation below and provide a valid project and instance
 * id that contains a BigTable instance to export.
 */
@Disabled
public class VandelayBigTableImporterTest {

  private static final String projectId = "test-project-id";
  private static final String instanceId = "test-instance-id";

  private static final String importConfigPath = "target/config.json";
  private static final String importDataPath = "target/data.json";

  @Test
  public void testImport() throws Exception {
    final VandelayJacksonSerializer<VandelayBigTableGCPConfiguration> configSerializer =
        new VandelayJacksonSerializer<>();
    final String configContent = Files.readString(Path.of(importConfigPath));
    final VandelayBigTableGCPConfiguration config =
        configSerializer.deserialize(configContent, VandelayBigTableGCPConfiguration.class);

    final VandelayJacksonSerializer<VandelayBigTableRowBatch> dataSerializer =
        new VandelayJacksonSerializer<>();
    final String dataContent = Files.readString(Path.of(importDataPath));
    final VandelayBigTableRowBatch rowBatch =
        dataSerializer.deserialize(dataContent, VandelayBigTableRowBatch.class);

    final VandelayBigTableImportConfiguration importConfiguration =
        new VandelayBigTableImportConfiguration(projectId, instanceId, config, true, true);

    final VandelayBigTableImporter importer =
        new VandelayBigTableImporter(new VandelayBigTableClient());
    Status status = importer.importFrom(importConfiguration);
    assertTrue(status.succeeded());

    status = importer.addRows(importConfiguration, rowBatch.getRows());
    assertTrue(status.succeeded());
  }
}
