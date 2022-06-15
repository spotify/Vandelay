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

package com.spotify.api.vandelay.core.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class StatusTest {

  @Test
  public void testSuccessStatus() {
    assertTrue(Status.success().succeeded());
    assertTrue(Status.success("message").succeeded());
    assertTrue(Status.success("message", new ArrayList<>()).succeeded());
  }

  @Test
  public void testFailStatus() {
    assertTrue(Status.fail().failed());
    assertTrue(Status.fail("message").failed());
    assertTrue(Status.fail("message", new ArrayList<>()).failed());
  }

  @Test
  public void testMessage() {
    assertEquals("message", Status.success("message").getMessage().get());
  }

  @Test
  public void testMetadata() {
    assertEquals(1, Status.success("message", 1).getMetadata().get());
  }
}
