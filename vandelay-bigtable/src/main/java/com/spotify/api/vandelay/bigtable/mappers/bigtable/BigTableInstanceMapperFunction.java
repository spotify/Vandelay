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

import com.google.cloud.bigtable.admin.v2.models.Instance;
import com.google.protobuf.InvalidProtocolBufferException;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableInstance;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import com.spotify.api.vandelay.core.util.Reflected;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableInstanceMapperFunction
    extends VandelayObjectMapperFunction<Instance, BigTableInstance> {

  private static final Logger logger =
      LoggerFactory.getLogger(BigTableInstanceMapperFunction.class);

  public BigTableInstanceMapperFunction() {
    super(Instance.class, BigTableInstance.class);
  }

  @Override
  protected Function<Instance, Optional<BigTableInstance>> convertToImpl() {
    return instance -> {
      final Optional<com.google.bigtable.admin.v2.Instance> proto =
          Reflected.getField(
              instance, Instance.class, "proto", com.google.bigtable.admin.v2.Instance.class);
      if (proto.isEmpty()) {
        logger.error("Instance has no proto field");
        return Optional.empty();
      }

      return Optional.of(
          new BigTableInstance(new Base64EncodedValue("instance", proto.get().toByteArray())));
    };
  }

  @Override
  protected Function<BigTableInstance, Optional<Instance>> convertFromImpl() {
    return bigTableInstance -> {
      try {
        com.google.bigtable.admin.v2.Instance proto =
            com.google.bigtable.admin.v2.Instance.parseFrom(
                bigTableInstance.getProtobuf().toDecodedBytes());
        return Optional.of(Instance.fromProto(proto));
      } catch (InvalidProtocolBufferException ex) {
        logger.error("Could not parse Instance from proto");
      }
      return Optional.empty();
    };
  }
}
