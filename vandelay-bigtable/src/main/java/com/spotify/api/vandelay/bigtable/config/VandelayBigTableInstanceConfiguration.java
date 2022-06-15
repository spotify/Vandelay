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
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableCluster;
import com.spotify.api.vandelay.bigtable.model.gcp.bigtable.BigTableInstance;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.core.meta.RequiredField;
import java.util.List;

public class VandelayBigTableInstanceConfiguration {

  @RequiredField private BigTableInstance instance;
  @RequiredField private List<BigTableCluster> clusters;
  @RequiredField private List<BigTableAppProfile> appProfiles;
  @RequiredField private IAMPolicy appPolicy;

  VandelayBigTableInstanceConfiguration() {}

  public VandelayBigTableInstanceConfiguration(
      final BigTableInstance instance,
      final List<BigTableCluster> clusters,
      final List<BigTableAppProfile> appProfiles,
      final IAMPolicy appPolicy) {
    this.instance = instance;
    this.clusters = clusters;
    this.appProfiles = appProfiles;
    this.appPolicy = appPolicy;
  }

  public BigTableInstance getInstance() {
    return instance;
  }

  public List<BigTableCluster> getClusters() {
    return clusters;
  }

  public List<BigTableAppProfile> getAppProfiles() {
    return appProfiles;
  }

  public IAMPolicy getAppPolicy() {
    return appPolicy;
  }
}
