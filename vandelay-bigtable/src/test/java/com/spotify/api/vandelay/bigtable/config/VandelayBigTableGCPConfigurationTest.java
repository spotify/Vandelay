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

public class VandelayBigTableGCPConfigurationTest {

  @Test
  public void testBasicGCPConfiguration() {

    final VandelayBigTableGCPConfiguration gcpConfiguration =
        VandelayGCPConfigurationUtil.getConfiguration();

    assertEquals("projectid", gcpConfiguration.getProjectId());
    assertEquals("instanceid", gcpConfiguration.getInstanceId());
    assertEquals(
        VandelayGCPConfigurationUtil.instance,
        gcpConfiguration.getInstanceConfiguration().getInstance());
    assertEquals(
        VandelayGCPConfigurationUtil.policy,
        gcpConfiguration.getInstanceConfiguration().getAppPolicy());
    assertEquals(
        VandelayGCPConfigurationUtil.appProfiles,
        gcpConfiguration.getInstanceConfiguration().getAppProfiles());
    assertEquals(
        VandelayGCPConfigurationUtil.clusters,
        gcpConfiguration.getInstanceConfiguration().getClusters());
    assertEquals(
        VandelayGCPConfigurationUtil.tables, gcpConfiguration.getTableConfiguration().getTables());
    assertEquals(
        VandelayGCPConfigurationUtil.tablePolicies,
        gcpConfiguration.getTableConfiguration().getTablePolicies());
    assertEquals(
        VandelayGCPConfigurationUtil.backups,
        gcpConfiguration.getTableConfiguration().getTableBackups());
    assertEquals(
        VandelayGCPConfigurationUtil.backupPolicies,
        gcpConfiguration.getTableConfiguration().getTableBackupPolicies());
  }

  @Test
  public void testGCPConfigurationSerialization() {
    final VandelayBigTableGCPConfiguration gcpConfiguration =
        VandelayGCPConfigurationUtil.getConfiguration();
    final VandelayJacksonSerializer<VandelayBigTableGCPConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final String expected =
        "{\"instanceId\":\"instanceid\",\"projectId\":\"projectid\",\"instanceConfiguration\":{\"instance\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"aW5zdGFuY2U=\"}},\"clusters\":[{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Y2x1c3Rlcg==\"}}],\"appProfiles\":[{\"id\":\"profileid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YXBwcHJvZmlsZQ==\"}}],\"appPolicy\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableConfiguration\":{\"tables\":{\"table\":{\"columnFamilies\":[{\"gcRule\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Z2NydWxl\"}},\"id\":\"id\"}],\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"replicationStateByClusterId\":{\"key\":\"value\"}}},\"tablePolicies\":{\"table\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableBackups\":{\"table\":[{\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YmFja3Vw\"}}]},\"tableBackupPolicies\":{\"cluster\":{\"table\":[{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}]}}}}";
    final String content = serializer.serialize(gcpConfiguration);
    assertEquals(expected, content);
  }

  @Test
  public void testGCPConfigurationDeserialization() {
    final String content =
        "{\"instanceId\":\"instanceid\",\"projectId\":\"projectid\",\"instanceConfiguration\":{\"instance\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"aW5zdGFuY2U=\"}},\"clusters\":[{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Y2x1c3Rlcg==\"}}],\"appProfiles\":[{\"id\":\"profileid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YXBwcHJvZmlsZQ==\"}}],\"appPolicy\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableConfiguration\":{\"tables\":{\"table\":{\"columnFamilies\":[{\"gcRule\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Z2NydWxl\"}},\"id\":\"id\"}],\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"replicationStateByClusterId\":{\"key\":\"value\"}}},\"tablePolicies\":{\"table\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}},\"tableBackups\":{\"table\":[{\"id\":\"tableid\",\"instanceId\":\"instanceid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YmFja3Vw\"}}]},\"tableBackupPolicies\":{\"cluster\":{\"table\":[{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}]}}}}";
    final VandelayJacksonSerializer<VandelayBigTableGCPConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final VandelayBigTableGCPConfiguration gcpConfiguration =
        serializer.deserialize(content, VandelayBigTableGCPConfiguration.class);

    assertEquals("projectid", gcpConfiguration.getProjectId());
    assertEquals("instanceid", gcpConfiguration.getInstanceId());
    assertNotNull(gcpConfiguration.getInstanceConfiguration().getInstance());
    assertNotNull(gcpConfiguration.getInstanceConfiguration().getAppPolicy());
    assertNotNull(gcpConfiguration.getInstanceConfiguration().getAppProfiles());
    assertNotNull(gcpConfiguration.getInstanceConfiguration().getClusters());
    assertNotNull(gcpConfiguration.getTableConfiguration().getTables());
    assertNotNull(gcpConfiguration.getTableConfiguration().getTablePolicies());
    assertNotNull(gcpConfiguration.getTableConfiguration().getTableBackups());
    assertNotNull(gcpConfiguration.getTableConfiguration().getTableBackupPolicies());
  }
}
