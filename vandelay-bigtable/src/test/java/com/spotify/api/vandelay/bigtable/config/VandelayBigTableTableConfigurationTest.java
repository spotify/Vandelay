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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.spotify.api.vandelay.bigtable.util.VandelayGCPConfigurationUtil;
import com.spotify.api.vandelay.serialization.jackson.VandelayJacksonSerializer;
import org.junit.jupiter.api.Test;

public class VandelayBigTableTableConfigurationTest {

  @Test
  public void testBasicTableConfiguration() {
    final VandelayBigTableTableConfiguration configuration =
        VandelayGCPConfigurationUtil.getConfiguration().getTableConfiguration();

    assertEquals(VandelayGCPConfigurationUtil.tables, configuration.getTables());
    assertEquals(VandelayGCPConfigurationUtil.tablePolicies, configuration.getTablePolicies());
    assertEquals(VandelayGCPConfigurationUtil.backups, configuration.getTableBackups());
    assertEquals(
        VandelayGCPConfigurationUtil.backupPolicies, configuration.getTableBackupPolicies());
  }

  @Test
  public void testTableConfigurationSerialization() {
    final VandelayBigTableTableConfiguration configuration =
        VandelayGCPConfigurationUtil.getConfiguration().getTableConfiguration();
    final VandelayJacksonSerializer<VandelayBigTableTableConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final String expected =
        "{\"tables\":{\"table\":{\"columnFamilies\":[{\"gcRule\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Z2NydWxl\"}},\"id\":\"id\"}],\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"replicationStateByClusterId\":{\"key\":\"value\"}}},\"tablePolicies\":{\"table\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableBackups\":{\"table\":[{\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YmFja3Vw\"}}]},\"tableBackupPolicies\":{\"cluster\":{\"table\":[{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}]}}}";
    final String content = serializer.serialize(configuration);
    assertEquals(expected, content);
  }

  @Test
  public void testTableConfigurationDeserialization() {
    final String content =
        "{\"tables\":{\"table\":{\"columnFamilies\":[{\"gcRule\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Z2NydWxl\"}},\"id\":\"id\"}],\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"replicationStateByClusterId\":{\"key\":\"value\"}}},\"tablePolicies\":{\"table\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableBackups\":{\"table\":[{\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YmFja3Vw\"}}]},\"tableBackupPolicies\":{\"cluster\":{\"table\":[{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}]}}}";
    final VandelayJacksonSerializer<VandelayBigTableTableConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final VandelayBigTableTableConfiguration configuration =
        serializer.deserialize(content, VandelayBigTableTableConfiguration.class);
    assertNotNull(configuration.getTables());
    assertNotNull(configuration.getTablePolicies());
    assertNotNull(configuration.getTableBackups());
    assertNotNull(configuration.getTableBackupPolicies());
  }
}
