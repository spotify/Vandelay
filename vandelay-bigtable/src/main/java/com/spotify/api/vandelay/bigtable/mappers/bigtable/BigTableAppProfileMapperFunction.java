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

import com.google.cloud.bigtable.admin.v2.models.AppProfile;
import com.google.protobuf.InvalidProtocolBufferException;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableAppProfile;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import com.spotify.api.vandelay.core.util.Reflected;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableAppProfileMapperFunction
    extends VandelayObjectMapperFunction<AppProfile, BigTableAppProfile> {

  private static final Logger logger =
      LoggerFactory.getLogger(BigTableAppProfileMapperFunction.class);

  public BigTableAppProfileMapperFunction() {
    super(AppProfile.class, BigTableAppProfile.class);
  }

  @Override
  protected Function<AppProfile, Optional<BigTableAppProfile>> convertToImpl() {
    return appProfile -> {
      final Optional<com.google.bigtable.admin.v2.AppProfile> proto =
          Reflected.getField(
              appProfile, AppProfile.class, "proto", com.google.bigtable.admin.v2.AppProfile.class);
      if (proto.isEmpty()) {
        logger.error("AppProfile has no proto field");
        return Optional.empty();
      }

      return Optional.of(
          new BigTableAppProfile(
              appProfile.getId(), new Base64EncodedValue("appprofile", proto.get().toByteArray())));
    };
  }

  @Override
  protected Function<BigTableAppProfile, Optional<AppProfile>> convertFromImpl() {
    return bigTableAppProfile -> {
      try {

        com.google.bigtable.admin.v2.AppProfile proto =
            com.google.bigtable.admin.v2.AppProfile.parseFrom(
                bigTableAppProfile.getProtobuf().toDecodedBytes());
        return Optional.of(AppProfile.fromProto(proto));
      } catch (InvalidProtocolBufferException ex) {
        logger.error("Could not parse AppProfile from proto");
      }
      return Optional.empty();
    };
  }
}
