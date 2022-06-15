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
import java.util.List;

public class IAMPolicy {

  @RequiredField private List<IAMBinding> bindingList;
  @RequiredField private String etag;
  @RequiredField private int version;

  IAMPolicy() {}

  public IAMPolicy(final String etag, final int version, final List<IAMBinding> bindingList) {
    this.etag = etag;
    this.version = version;
    this.bindingList = bindingList;
  }

  public List<IAMBinding> getBindingList() {
    return bindingList;
  }

  public String getEtag() {
    return etag;
  }

  public int getVersion() {
    return version;
  }
}
