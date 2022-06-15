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

package com.spotify.api.vandelay.bigtable.model.gcp.bigtable;

import com.spotify.api.vandelay.core.meta.RequiredField;
import java.util.List;
import java.util.Map;

public class BigTableTable {

  @RequiredField private List<BigTableColumnFamily> columnFamilies;
  @RequiredField private String id;
  @RequiredField private String instanceId;
  @RequiredField private Map<String, String> replicationStateByClusterId;

  BigTableTable() {}

  public BigTableTable(
      final String id,
      final String instanceId,
      final List<BigTableColumnFamily> columnFamilies,
      final Map<String, String> replicationStateByClusterId) {
    this.id = id;
    this.instanceId = instanceId;
    this.columnFamilies = columnFamilies;
    this.replicationStateByClusterId = replicationStateByClusterId;
  }

  public List<BigTableColumnFamily> getColumnFamilies() {
    return columnFamilies;
  }

  public String getId() {
    return id;
  }

  public String getInstanceId() {
    return instanceId;
  }

  public Map<String, String> getReplicationStateByClusterId() {
    return replicationStateByClusterId;
  }
}
