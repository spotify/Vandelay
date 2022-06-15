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

import com.google.bigtable.admin.v2.GcRule;
import com.google.cloud.bigtable.admin.v2.models.ColumnFamily;
import com.google.protobuf.InvalidProtocolBufferException;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableColumnFamily;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableGCRule;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableColumnFamilyMapperFunction
    extends VandelayObjectMapperFunction<ColumnFamily, BigTableColumnFamily> {

  private static final Logger logger =
      LoggerFactory.getLogger(BigTableColumnFamilyMapperFunction.class);

  public BigTableColumnFamilyMapperFunction() {
    super(ColumnFamily.class, BigTableColumnFamily.class);
  }

  @Override
  protected Function<ColumnFamily, Optional<BigTableColumnFamily>> convertToImpl() {
    return columnFamily -> {
      final BigTableGCRuleMapperFunction gcConverter = new BigTableGCRuleMapperFunction();
      final Optional<BigTableGCRule> gcRule = gcConverter.convertTo(columnFamily.getGCRule());
      if (gcRule.isEmpty()) {
        logger.error("GCRule for column family is missing");
        return Optional.empty();
      }

      return Optional.of(new BigTableColumnFamily(columnFamily.getId(), gcRule.get()));
    };
  }

  @Override
  protected Function<BigTableColumnFamily, Optional<ColumnFamily>> convertFromImpl() {
    return bigTableColumnFamily -> {
      try {
        GcRule gcRule =
            GcRule.parseFrom(bigTableColumnFamily.getGcRule().getProtobuf().toDecodedBytes());
        return Optional.of(
            ColumnFamily.fromProto(
                bigTableColumnFamily.getId(),
                com.google.bigtable.admin.v2.ColumnFamily.newBuilder().setGcRule(gcRule).build()));
      } catch (InvalidProtocolBufferException ex) {
        logger.error("Could not parse ColumnFamily from proto");
      }
      return Optional.empty();
    };
  }
}
