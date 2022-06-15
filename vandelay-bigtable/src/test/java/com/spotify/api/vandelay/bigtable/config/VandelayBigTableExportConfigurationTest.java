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

package com.spotify.api.vandelay.bigtable.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spotify.api.vandelay.serialization.jackson.VandelayJacksonSerializer;
import org.junit.jupiter.api.Test;

public class VandelayBigTableExportConfigurationTest {

  @Test
  public void testBasicExportConfiguration() {
    final VandelayBigTableExportConfiguration exportConfiguration =
        new VandelayBigTableExportConfiguration("projectid", "instanceid", false, true);
    assertEquals("projectid", exportConfiguration.getProjectId());
    assertEquals("instanceid", exportConfiguration.getInstanceId());
    assertFalse(exportConfiguration.getDeleteOnExport());
    assertTrue(exportConfiguration.getParallelExport());
  }

  @Test
  public void testExportConfigurationSerialization() {
    final VandelayBigTableExportConfiguration exportConfiguration =
        new VandelayBigTableExportConfiguration("projectid", "instanceid", false, true);
    final VandelayJacksonSerializer<VandelayBigTableExportConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final String configuration = serializer.serialize(exportConfiguration);
    assertEquals(
        "{\"projectId\":\"projectid\",\"instanceId\":\"instanceid\",\"deleteOnExport\":false,\"parallelExport\":true}",
        configuration);
  }

  @Test
  public void testExportConfigurationDeserialization() {
    final String configuration =
        "{\"projectId\":\"projectid\",\"instanceId\":\"instanceid\",\"deleteOnExport\":false,\"parallelExport\":true}";
    final VandelayJacksonSerializer<VandelayBigTableExportConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final VandelayBigTableExportConfiguration exportConfiguration =
        serializer.deserialize(configuration, VandelayBigTableExportConfiguration.class);
    assertEquals("projectid", exportConfiguration.getProjectId());
    assertEquals("instanceid", exportConfiguration.getInstanceId());
    assertFalse(exportConfiguration.getDeleteOnExport());
    assertTrue(exportConfiguration.getParallelExport());
  }
}
