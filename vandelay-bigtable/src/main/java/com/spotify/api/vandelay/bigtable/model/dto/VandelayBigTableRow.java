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
import java.util.List;

public class VandelayBigTableRow {

  @RequiredField private String table;
  @RequiredField private String rowKey;
  @RequiredField private List<VandelayBigTableCell> rowCells;

  VandelayBigTableRow() {}

  public VandelayBigTableRow(
      final String table, final String rowKey, final List<VandelayBigTableCell> rowCells) {
    this.table = table;
    this.rowKey = rowKey;
    this.rowCells = rowCells;
  }

  public String getTable() {
    return table;
  }

  public String getRowKey() {
    return rowKey;
  }

  public List<VandelayBigTableCell> getRowCells() {
    return rowCells;
  }
}
