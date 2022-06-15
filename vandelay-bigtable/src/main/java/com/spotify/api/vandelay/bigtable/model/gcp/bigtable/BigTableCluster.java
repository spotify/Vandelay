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
import com.spotify.api.vandelay.core.model.Base64EncodedValue;

public class BigTableCluster {

  @RequiredField private Base64EncodedValue protobuf;

  BigTableCluster() {}

  public BigTableCluster(final Base64EncodedValue proto) {
    this.protobuf = proto;
  }

  public Base64EncodedValue getProtobuf() {
    return protobuf;
  }
}
