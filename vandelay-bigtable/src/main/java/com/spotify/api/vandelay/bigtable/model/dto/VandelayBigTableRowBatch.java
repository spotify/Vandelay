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
import java.util.ArrayList;
import java.util.List;

public class VandelayBigTableRowBatch {

  @RequiredField private List<VandelayBigTableRow> rows = new ArrayList<>();

  public VandelayBigTableRowBatch() {}

  public VandelayBigTableRowBatch(final VandelayBigTableRow row) {
    this.rows.add(row);
  }

  public VandelayBigTableRowBatch(final List<VandelayBigTableRow> rows) {
    this.rows = rows;
  }

  public void addRow(final VandelayBigTableRow row) {
    this.rows.add(row);
  }

  public List<VandelayBigTableRow> getRows() {
    return rows;
  }
}
