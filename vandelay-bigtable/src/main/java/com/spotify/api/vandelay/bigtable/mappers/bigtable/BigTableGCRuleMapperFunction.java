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

package com.spotify.api.vandelay.bigtable.mappers.bigtable;

import com.google.cloud.bigtable.admin.v2.models.GCRules;
import com.google.cloud.bigtable.admin.v2.models.GCRules.GCRule;
import com.google.protobuf.InvalidProtocolBufferException;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableGCRule;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableGCRuleMapperFunction
    extends VandelayObjectMapperFunction<GCRule, BigTableGCRule> {

  private static final Logger logger = LoggerFactory.getLogger(BigTableGCRuleMapperFunction.class);

  public BigTableGCRuleMapperFunction() {
    super(GCRule.class, BigTableGCRule.class);
  }

  @Override
  protected Function<GCRule, Optional<BigTableGCRule>> convertToImpl() {
    return gcRule ->
        Optional.of(
            new BigTableGCRule(new Base64EncodedValue("gcrule", gcRule.toProto().toByteArray())));
  }

  @Override
  protected Function<BigTableGCRule, Optional<GCRule>> convertFromImpl() {
    return bigTableGCRule -> {
      try {
        com.google.bigtable.admin.v2.GcRule gcRule =
            com.google.bigtable.admin.v2.GcRule.parseFrom(
                bigTableGCRule.getProtobuf().toDecodedBytes());
        return Optional.of(GCRules.GCRULES.fromProto(gcRule));
      } catch (InvalidProtocolBufferException ex) {
        logger.error("Could not parse GCRule from proto");
      }
      return Optional.empty();
    };
  }
}
