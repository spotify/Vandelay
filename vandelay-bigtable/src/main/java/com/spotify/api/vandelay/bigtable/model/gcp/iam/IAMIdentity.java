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

package com.spotify.api.vandelay.bigtable.model.gcp.iam;

import com.spotify.api.vandelay.core.meta.RequiredField;

public class IAMIdentity {

  @RequiredField private String value;
  @RequiredField private String type;

  IAMIdentity() {}

  public IAMIdentity(final String value, final String type) {
    this.value = value;
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public String getType() {
    return type;
  }
}
