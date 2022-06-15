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

package com.spotify.api.vandelay.bigtable.util;

import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration.Builder;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableAppProfile;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableBackup;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableCluster;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableColumnFamily;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableGCRule;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableInstance;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableTable;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMBinding;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class VandelayGCPConfigurationUtil {

  public static final BigTableInstance instance =
      new BigTableInstance(
          new Base64EncodedValue("test", "instance".getBytes(StandardCharsets.UTF_8)));
  public static final List<BigTableCluster> clusters =
      List.of(
          new BigTableCluster(
              new Base64EncodedValue("test", "cluster".getBytes(StandardCharsets.UTF_8))));
  public static final List<BigTableAppProfile> appProfiles =
      List.of(
          new BigTableAppProfile(
              "profileid",
              new Base64EncodedValue("test", "appprofile".getBytes(StandardCharsets.UTF_8))));
  public static final IAMPolicy policy =
      new IAMPolicy("etag", 1, List.of(new IAMBinding("role", List.of("testmember"), null)));
  public static final Map<String, BigTableTable> tables =
      Map.of(
          "table",
          new BigTableTable(
              "tableid",
              "instanceid",
              List.of(
                  new BigTableColumnFamily(
                      "id",
                      new BigTableGCRule(
                          new Base64EncodedValue(
                              "test", "gcrule".getBytes(StandardCharsets.UTF_8))))),
              Map.of("key", "value")));
  public static final Map<String, IAMPolicy> tablePolicies = Map.of("table", policy);
  public static final Map<String, List<BigTableBackup>> backups =
      Map.of(
          "table",
          List.of(
              new BigTableBackup(
                  "tableid",
                  "instanceid",
                  new Base64EncodedValue("test", "backup".getBytes(StandardCharsets.UTF_8)))));
  public static final Map<String, Map<String, List<IAMPolicy>>> backupPolicies =
      Map.of("cluster", Map.of("table", List.of(policy)));

  public static VandelayBigTableGCPConfiguration getConfiguration() {
    return new Builder("projectid", "instanceid")
        .setClusters(clusters)
        .setInstance(instance)
        .setInstanceAppProfiles(appProfiles)
        .setInstancePolicies(policy)
        .setTables(tables)
        .setTablePolicies(tablePolicies)
        .setTableBackup(backups)
        .setTableBackupPolicies(backupPolicies)
        .build();
  }
}
