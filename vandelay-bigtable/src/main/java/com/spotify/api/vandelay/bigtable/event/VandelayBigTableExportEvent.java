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

package com.spotify.api.vandelay.bigtable.event;

import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRow;
import com.spotify.api.vandelay.core.event.VandelayEvent;
import java.util.Optional;

public class VandelayBigTableExportEvent {

  private VandelayEvent<VandelayBigTableRow> rowExportEvent;
  private VandelayEvent<VandelayBigTableGCPConfiguration> infoEvent;
  private VandelayEvent<VandelayBigTableRowsExportFinishedEvent> exportFinishedEvent;
  private VandelayEvent<VandelayBigTableDeleteInstanceEvent> deleteInstanceEvent;

  public VandelayBigTableExportEvent(final VandelayBigTableRow rowExportEvent) {
    this.rowExportEvent = new VandelayEvent<>(rowExportEvent);
  }

  public VandelayBigTableExportEvent(final VandelayBigTableGCPConfiguration infoEvent) {
    this.infoEvent = new VandelayEvent<>(infoEvent);
  }

  public VandelayBigTableExportEvent(
      final VandelayBigTableDeleteInstanceEvent deleteInstanceEvent) {
    this.deleteInstanceEvent = new VandelayEvent<>(deleteInstanceEvent);
  }

  public VandelayBigTableExportEvent(
      final VandelayBigTableRowsExportFinishedEvent exportFinishedEvent) {
    this.exportFinishedEvent = new VandelayEvent<>(exportFinishedEvent);
  }

  public Optional<VandelayEvent<VandelayBigTableRow>> getRowExportEvent() {
    return Optional.ofNullable(rowExportEvent);
  }

  public Optional<VandelayEvent<VandelayBigTableGCPConfiguration>> getInfoEvent() {
    return Optional.ofNullable(infoEvent);
  }

  public Optional<VandelayEvent<VandelayBigTableRowsExportFinishedEvent>> getExportFinishedEvent() {
    return Optional.ofNullable(exportFinishedEvent);
  }

  public Optional<VandelayEvent<VandelayBigTableDeleteInstanceEvent>> getDeleteEvent() {
    return Optional.ofNullable(deleteInstanceEvent);
  }
}
