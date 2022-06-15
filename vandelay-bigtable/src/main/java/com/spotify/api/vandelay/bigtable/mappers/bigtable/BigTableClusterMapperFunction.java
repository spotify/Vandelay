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

import com.google.cloud.bigtable.admin.v2.models.Cluster;
import com.google.protobuf.InvalidProtocolBufferException;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableCluster;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import com.spotify.api.vandelay.core.util.Reflected;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableClusterMapperFunction
    extends VandelayObjectMapperFunction<Cluster, BigTableCluster> {

  private static final Logger logger = LoggerFactory.getLogger(BigTableClusterMapperFunction.class);

  public BigTableClusterMapperFunction() {
    super(Cluster.class, BigTableCluster.class);
  }

  @Override
  protected Function<Cluster, Optional<BigTableCluster>> convertToImpl() {
    return cluster -> {
      final Optional<com.google.bigtable.admin.v2.Cluster> proto =
          Reflected.getField(
              cluster, Cluster.class, "stateProto", com.google.bigtable.admin.v2.Cluster.class);
      if (proto.isEmpty()) {
        logger.error("Cluster has no stateProto field");
        return Optional.empty();
      }

      return Optional.of(
          new BigTableCluster(new Base64EncodedValue("proto", proto.get().toByteArray())));
    };
  }

  @Override
  protected Function<BigTableCluster, Optional<Cluster>> convertFromImpl() {
    return bigTableCluster -> {
      try {
        com.google.bigtable.admin.v2.Cluster proto =
            com.google.bigtable.admin.v2.Cluster.parseFrom(
                bigTableCluster.getProtobuf().toDecodedBytes());
        return Optional.of(Cluster.fromProto(proto));
      } catch (InvalidProtocolBufferException ex) {
        logger.error("Could not parse Cluster from proto");
      }
      return Optional.empty();
    };
  }
}
