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

package com.spotify.api.vandelay.bigtable.model.dto;

import com.spotify.api.vandelay.core.meta.RequiredField;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import java.util.List;

public class VandelayBigTableCell {

  @RequiredField private String columnFamily;
  @RequiredField private Base64EncodedValue qualifier;
  @RequiredField private long timestamp;
  @RequiredField private Base64EncodedValue value;
  @RequiredField private List<String> labels;

  VandelayBigTableCell() {}

  public VandelayBigTableCell(
      final String columnFamily,
      final Base64EncodedValue qualifier,
      final long timestamp,
      final Base64EncodedValue value,
      final List<String> labels) {
    this.columnFamily = columnFamily;
    this.qualifier = qualifier;
    this.timestamp = timestamp;
    this.value = value;
    this.labels = labels;
  }

  public String getColumnFamily() {
    return columnFamily;
  }

  public Base64EncodedValue getQualifier() {
    return qualifier;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Base64EncodedValue getValue() {
    return value;
  }

  public List<String> getLabels() {
    return labels;
  }
}
