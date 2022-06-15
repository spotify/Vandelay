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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spotify.api.vandelay.bigtable.util.VandelayGCPConfigurationUtil;
import com.spotify.api.vandelay.serialization.jackson.VandelayJacksonSerializer;
import org.junit.jupiter.api.Test;

public class VandelayBigTableImportConfigurationTest {

  @Test
  public void testBasicImportConfiguration() {
    final VandelayBigTableGCPConfiguration gcpConfigurationContainer =
        VandelayGCPConfigurationUtil.getConfiguration();
    final VandelayBigTableImportConfiguration importConfiguration =
        new VandelayBigTableImportConfiguration(
            "projectid", "instanceid", gcpConfigurationContainer, false, true);
    assertEquals("projectid", importConfiguration.getProjectId());
    assertEquals("instanceid", importConfiguration.getInstanceId());
    assertNotNull(importConfiguration.getGcpConfiguration());
    assertFalse(importConfiguration.getCreateInstanceIfDoesntExist());
    assertTrue(importConfiguration.getCreateTableIfDoesntExist());
  }

  @Test
  public void testImportConfigurationSerialization() {
    final VandelayBigTableGCPConfiguration gcpConfigurationContainer =
        VandelayGCPConfigurationUtil.getConfiguration();
    final VandelayBigTableImportConfiguration importConfiguration =
        new VandelayBigTableImportConfiguration(
            "projectid", "instanceid", gcpConfigurationContainer, false, true);

    final VandelayJacksonSerializer<VandelayBigTableImportConfiguration> serializer =
        new VandelayJacksonSerializer<>();
    final String content = serializer.serialize(importConfiguration);

    final String expected =
        "{\"projectId\":\"projectid\",\"instanceId\":\"instanceid\",\"gcpConfiguration\":{\"instanceId\":\"instanceid\",\"projectId\":\"projectid\",\"instanceConfiguration\":{\"instance\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"aW5zdGFuY2U=\"}},\"clusters\":[{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Y2x1c3Rlcg==\"}}],\"appProfiles\":[{\"id\":\"profileid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YXBwcHJvZmlsZQ==\"}}],\"appPolicy\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableConfiguration\":{\"tables\":{\"table\":{\"columnFamilies\":[{\"gcRule\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Z2NydWxl\"}},\"id\":\"id\"}],\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"replicationStateByClusterId\":{\"key\":\"value\"}}},\"tablePolicies\":{\"table\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableBackups\":{\"table\":[{\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YmFja3Vw\"}}]},\"tableBackupPolicies\":{\"cluster\":{\"table\":[{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}]}}}},\"createInstanceIfDoesntExist\":false,\"createTableIfDoesntExist\":true}";
    assertEquals(expected, content);
  }

  @Test
  public void testImportConfigurationDeserialization() {

    final String content =
        "{\"projectId\":\"projectid\",\"instanceId\":\"instanceid\",\"gcpConfiguration\":{\"instanceId\":\"instanceid\",\"projectId\":\"projectid\",\"instanceConfiguration\":{\"instance\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"aW5zdGFuY2U=\"}},\"clusters\":[{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Y2x1c3Rlcg==\"}}],\"appProfiles\":[{\"id\":\"profileid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YXBwcHJvZmlsZQ==\"}}],\"appPolicy\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableConfiguration\":{\"tables\":{\"table\":{\"columnFamilies\":[{\"gcRule\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Z2NydWxl\"}},\"id\":\"id\"}],\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"replicationStateByClusterId\":{\"key\":\"value\"}}},\"tablePolicies\":{\"table\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableBackups\":{\"table\":[{\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YmFja3Vw\"}}]},\"tableBackupPolicies\":{\"cluster\":{\"table\":[{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}]}}}},\"createInstanceIfDoesntExist\":false,\"createTableIfDoesntExist\":true}";
    final VandelayJacksonSerializer<VandelayBigTableImportConfiguration> serializer =
        new VandelayJacksonSerializer<>();
    final VandelayBigTableImportConfiguration importConfiguration =
        serializer.deserialize(content, VandelayBigTableImportConfiguration.class);

    assertEquals("projectid", importConfiguration.getProjectId());
    assertEquals("instanceid", importConfiguration.getInstanceId());
    assertNotNull(importConfiguration.getGcpConfiguration());
    assertFalse(importConfiguration.getCreateInstanceIfDoesntExist());
    assertTrue(importConfiguration.getCreateTableIfDoesntExist());
  }
}
