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

public class IAMCondition {

  @RequiredField private String description;
  @RequiredField private String expression;
  @RequiredField private String title;

  IAMCondition() {}

  public IAMCondition(final String title, final String description, final String expression) {
    this.title = title;
    this.description = description;
    this.expression = expression;
  }

  public String getDescription() {
    return description;
  }

  public String getExpression() {
    return expression;
  }

  public String getTitle() {
    return title;
  }
}
