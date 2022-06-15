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

import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableBackup;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableTable;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.core.meta.RequiredField;
import java.util.List;
import java.util.Map;

public class VandelayBigTableTableConfiguration {

  @RequiredField private Map<String /*Table Id*/, BigTableTable> tables;
  @RequiredField private Map<String /*Table Id*/, IAMPolicy> tablePolicies;
  @RequiredField private Map<String /*Cluster Id*/, List<BigTableBackup>> tableBackups;

  @RequiredField
  private Map<String /*Cluster Id*/, Map<String /*Backup Id*/, List<IAMPolicy>>>
      tableBackupPolicies;

  VandelayBigTableTableConfiguration() {}

  public VandelayBigTableTableConfiguration(
      final Map<String, BigTableTable> tables,
      final Map<String, IAMPolicy> tablePolicies,
      final Map<String, List<BigTableBackup>> tableBackups,
      final Map<String, Map<String, List<IAMPolicy>>> tableBackupPolicies) {
    this.tables = tables;
    this.tablePolicies = tablePolicies;
    this.tableBackups = tableBackups;
    this.tableBackupPolicies = tableBackupPolicies;
  }

  public Map<String, BigTableTable> getTables() {
    return tables;
  }

  public Map<String, IAMPolicy> getTablePolicies() {
    return tablePolicies;
  }

  public Map<String, List<BigTableBackup>> getTableBackups() {
    return tableBackups;
  }

  public Map<String, Map<String, List<IAMPolicy>>> getTableBackupPolicies() {
    return tableBackupPolicies;
  }
}
