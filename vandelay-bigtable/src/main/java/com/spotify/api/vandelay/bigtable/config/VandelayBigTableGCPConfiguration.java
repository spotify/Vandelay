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

import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableAppProfile;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableBackup;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableCluster;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableInstance;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableTable;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.core.meta.RequiredField;
import java.util.List;
import java.util.Map;

public class VandelayBigTableGCPConfiguration {

  @RequiredField private String instanceId;
  @RequiredField private String projectId;

  @RequiredField private VandelayBigTableInstanceConfiguration instanceConfiguration;
  @RequiredField private VandelayBigTableTableConfiguration tableConfiguration;

  VandelayBigTableGCPConfiguration() {}

  private VandelayBigTableGCPConfiguration(final VandelayBigTableGCPConfiguration.Builder builder) {
    this.instanceId = builder.instanceId;
    this.projectId = builder.projectId;

    this.instanceConfiguration =
        new VandelayBigTableInstanceConfiguration(
            builder.instances,
            builder.instanceClusters,
            builder.instanceAppProfiles,
            builder.instancePolicy);
    this.tableConfiguration =
        new VandelayBigTableTableConfiguration(
            builder.tables,
            builder.tablePolicies,
            builder.tableBackups,
            builder.tableBackupPolicies);
  }

  public String getInstanceId() {
    return instanceId;
  }

  public String getProjectId() {
    return projectId;
  }

  public VandelayBigTableInstanceConfiguration getInstanceConfiguration() {
    return instanceConfiguration;
  }

  public VandelayBigTableTableConfiguration getTableConfiguration() {
    return tableConfiguration;
  }

  public static class Builder {

    private final String instanceId;
    private final String projectId;

    private BigTableInstance instances;
    private List<BigTableAppProfile> instanceAppProfiles;
    private List<BigTableCluster> instanceClusters;
    private IAMPolicy instancePolicy;

    private Map<String, BigTableTable> tables;
    private Map<String, List<BigTableBackup>> tableBackups;
    private Map<String, Map<String, List<IAMPolicy>>> tableBackupPolicies;
    private Map<String, IAMPolicy> tablePolicies;

    public Builder(final String projectId, final String instanceId) {
      this.instanceId = instanceId;
      this.projectId = projectId;
    }

    public Builder setInstance(final BigTableInstance instances) {
      this.instances = instances;
      return this;
    }

    public Builder setInstanceAppProfiles(final List<BigTableAppProfile> instanceAppProfiles) {
      this.instanceAppProfiles = instanceAppProfiles;
      return this;
    }

    public Builder setClusters(final List<BigTableCluster> instanceClusters) {
      this.instanceClusters = instanceClusters;
      return this;
    }

    public Builder setInstancePolicies(final IAMPolicy instancePolicy) {
      this.instancePolicy = instancePolicy;
      return this;
    }

    public Builder setTables(final Map<String, BigTableTable> tables) {
      this.tables = tables;
      return this;
    }

    public Builder setTableBackup(final Map<String, List<BigTableBackup>> tableBackups) {
      this.tableBackups = tableBackups;
      return this;
    }

    public Builder setTableBackupPolicies(
        final Map<String, Map<String, List<IAMPolicy>>> tableBackupPolicies) {
      this.tableBackupPolicies = tableBackupPolicies;
      return this;
    }

    public Builder setTablePolicies(final Map<String, IAMPolicy> tablePolicies) {
      this.tablePolicies = tablePolicies;
      return this;
    }

    public VandelayBigTableGCPConfiguration build() {
      return new VandelayBigTableGCPConfiguration(this);
    }
  }
}
