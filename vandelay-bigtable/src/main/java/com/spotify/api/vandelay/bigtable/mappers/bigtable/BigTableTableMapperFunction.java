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

import com.google.bigtable.admin.v2.TableName;
import com.google.cloud.bigtable.admin.v2.models.ColumnFamily;
import com.google.cloud.bigtable.admin.v2.models.Table;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableColumnFamily;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableTable;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigTableTableMapperFunction
    extends VandelayObjectMapperFunction<Table, BigTableTable> {

  private static final Logger logger = LoggerFactory.getLogger(BigTableTableMapperFunction.class);

  public BigTableTableMapperFunction() {
    super(Table.class, BigTableTable.class);
  }

  @Override
  protected Function<Table, Optional<BigTableTable>> convertToImpl() {
    return table -> {
      final BigTableColumnFamilyMapperFunction columnFamilyConverter =
          new BigTableColumnFamilyMapperFunction();
      final Map<String, String> transformedReplicationStateMap =
          table.getReplicationStatesByClusterId().entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey, entry -> entry.getValue().toProto().toString()));
      final List<BigTableColumnFamily> transformedColumnFamily =
          table.getColumnFamilies().stream()
              .map(
                  x -> {
                    final Optional<BigTableColumnFamily> columnFamily =
                        columnFamilyConverter.convertTo(x);
                    if (columnFamily.isEmpty()) {
                      logger.error("Column family could not be converted");
                      return null;
                    }
                    return columnFamily.get();
                  })
              .filter(Objects::nonNull)
              .toList();
      return Optional.of(
          new BigTableTable(
              table.getId(),
              table.getInstanceId(),
              transformedColumnFamily,
              transformedReplicationStateMap));
    };
  }

  @Override
  protected Function<BigTableTable, Optional<Table>> convertFromImpl() {
    return bigTableTable -> {
      try {
        final TableName tableName =
            TableName.of("Project", bigTableTable.getInstanceId(), bigTableTable.getId());

        final List<ColumnFamily> columnFamilies =
            bigTableTable.getColumnFamilies().stream()
                .map(x -> new BigTableColumnFamilyMapperFunction().convertFromImpl().apply(x))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        final Map<String, Table.ReplicationState> replicationStatesByClusterId =
            bigTableTable.getReplicationStateByClusterId().entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey, x -> Table.ReplicationState.valueOf(x.getValue())));
        Constructor<Table> constructor = Table.class.getDeclaredConstructor(Object.class);
        constructor.setAccessible(true);
        return Optional.of(
            constructor.newInstance(tableName, replicationStatesByClusterId, columnFamilies));
      } catch (NoSuchMethodException ex) {
        logger.error("Could not access constructor of object", ex);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException ex) {
        logger.error("Could not construct object", ex);
      }
      return Optional.empty();
    };
  }
}
