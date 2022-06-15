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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spotify.api.vandelay.bigtable.config.VandelayBigTableGCPConfiguration;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableCell;
import com.spotify.api.vandelay.bigtable.model.dto.VandelayBigTableRow;
import com.spotify.api.vandelay.bigtable.util.VandelayGCPConfigurationUtil;
import com.spotify.api.vandelay.core.model.Base64EncodedValue;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VandelayBigTableExportEventTest {

  @Test
  public void testRowExportEvent() {
    final List<VandelayBigTableCell> rowCells =
        List.of(
            new VandelayBigTableCell(
                "cf",
                new Base64EncodedValue("test", "qualifier".getBytes(StandardCharsets.UTF_8)),
                123,
                new Base64EncodedValue("test", "value".getBytes(StandardCharsets.UTF_8)),
                List.of("label1")));

    final VandelayBigTableRow row = new VandelayBigTableRow("table", "rowkey", rowCells);
    final VandelayBigTableExportEvent event = new VandelayBigTableExportEvent(row);

    assertTrue(event.getRowExportEvent().isPresent());
    assertEquals("table", event.getRowExportEvent().get().getValue().getTable());
    assertEquals("rowkey", event.getRowExportEvent().get().getValue().getRowKey());
    assertEquals(rowCells, event.getRowExportEvent().get().getValue().getRowCells());
  }

  @Test
  public void testInfoEvent() {
    final VandelayBigTableGCPConfiguration configuration =
        VandelayGCPConfigurationUtil.getConfiguration();
    final VandelayBigTableExportEvent event = new VandelayBigTableExportEvent(configuration);

    assertTrue(event.getInfoEvent().isPresent());
    assertEquals(configuration, event.getInfoEvent().get().getValue());
  }

  @Test
  public void testDeleteInstanceEvent() {
    final VandelayBigTableDeleteInstanceEvent delete =
        new VandelayBigTableDeleteInstanceEvent("projectid", "instanceid");
    final VandelayBigTableExportEvent event = new VandelayBigTableExportEvent(delete);

    assertTrue(event.getDeleteEvent().isPresent());
    assertEquals("projectid", event.getDeleteEvent().get().getValue().getProjectId());
    assertEquals("instanceid", event.getDeleteEvent().get().getValue().getInstanceId());
  }

  @Test
  public void testExportFinishedEvent() {
    final VandelayBigTableRowsExportFinishedEvent finished =
        new VandelayBigTableRowsExportFinishedEvent("instanceid");
    final VandelayBigTableExportEvent event = new VandelayBigTableExportEvent(finished);

    assertTrue(event.getExportFinishedEvent().isPresent());
    assertEquals("instanceid", event.getExportFinishedEvent().get().getValue().getInstanceId());
  }
}
