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

public class VandelayBigTableInstanceConfigurationTest {

  @Test
  public void testBasicInstanceConfiguration() {
    final VandelayBigTableInstanceConfiguration configuration =
        VandelayGCPConfigurationUtil.getConfiguration().getInstanceConfiguration();

    assertEquals(VandelayGCPConfigurationUtil.instance, configuration.getInstance());
    assertEquals(VandelayGCPConfigurationUtil.policy, configuration.getAppPolicy());
    assertEquals(VandelayGCPConfigurationUtil.appProfiles, configuration.getAppProfiles());
    assertEquals(VandelayGCPConfigurationUtil.clusters, configuration.getClusters());
  }

  @Test
  public void testInstanceConfigurationSerialization() {
    final VandelayBigTableInstanceConfiguration configuration =
        VandelayGCPConfigurationUtil.getConfiguration().getInstanceConfiguration();
    final VandelayJacksonSerializer<VandelayBigTableInstanceConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final String expected =
        "{\"instance\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"aW5zdGFuY2U=\"}},\"clusters\":[{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Y2x1c3Rlcg==\"}}],\"appProfiles\":[{\"id\":\"profileid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YXBwcHJvZmlsZQ==\"}}],\"appPolicy\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}}";
    final String content = serializer.serialize(configuration);
    assertEquals(expected, content);
  }

  @Test
  public void testInstanceConfigurationDeserialization() {
    final String content =
        "{\"instance\":{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"aW5zdGFuY2U=\"}},\"clusters\":[{\"protobuf\":{\"name\":\"test\",\"base64Content\":\"Y2x1c3Rlcg==\"}}],\"appProfiles\":[{\"id\":\"profileid\",\"protobuf\":{\"name\":\"test\",\"base64Content\":\"YXBwcHJvZmlsZQ==\"}}],\"appPolicy\":{\"bindingList\":[{\"condition\":null,\"role\":\"role\",\"members\":[\"testmember\"]}],\"etag\":\"etag\",\"version\":1}}";
    final VandelayJacksonSerializer<VandelayBigTableInstanceConfiguration> serializer =
        new VandelayJacksonSerializer<>();

    final VandelayBigTableInstanceConfiguration configuration =
        serializer.deserialize(content, VandelayBigTableInstanceConfiguration.class);

    assertNotNull(configuration.getInstance());
    assertNotNull(configuration.getAppPolicy());
    assertNotNull(configuration.getAppProfiles());
    assertNotNull(configuration.getClusters());
  }
}
