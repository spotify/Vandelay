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

import com.google.cloud.bigtable.admin.v2.models.Backup;
import com.google.protobuf.InvalidProtocolBufferException;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableBackup;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import com.spotify.api.vandelay.core.util.Reflected;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableBackupMapperFunction
    extends VandelayObjectMapperFunction<Backup, BigTableBackup> {

  private static final Logger logger = LoggerFactory.getLogger(BigTableBackupMapperFunction.class);

  public BigTableBackupMapperFunction() {
    super(Backup.class, BigTableBackup.class);
  }

  @Override
  protected Function<Backup, Optional<BigTableBackup>> convertToImpl() {
    return backup -> {
      final Optional<com.google.bigtable.admin.v2.Backup> proto =
          Reflected.getField(
              backup, Backup.class, "proto", com.google.bigtable.admin.v2.Backup.class);
      if (proto.isEmpty()) {
        logger.error("Backup has no proto field");
        return Optional.empty();
      }

      return Optional.of(
          new BigTableBackup(
              backup.getId(),
              backup.getInstanceId(),
              new Base64EncodedValue("backup", proto.get().toByteArray())));
    };
  }

  @Override
  protected Function<BigTableBackup, Optional<Backup>> convertFromImpl() {
    return bigTableBackup -> {
      try {
        com.google.bigtable.admin.v2.Backup proto =
            com.google.bigtable.admin.v2.Backup.parseFrom(
                bigTableBackup.getProtobuf().toDecodedBytes());
        return Optional.of(Backup.fromProto(proto));
      } catch (InvalidProtocolBufferException ex) {
        logger.error("Could not parse Backup from proto");
      }
      return Optional.empty();
    };
  }
}
