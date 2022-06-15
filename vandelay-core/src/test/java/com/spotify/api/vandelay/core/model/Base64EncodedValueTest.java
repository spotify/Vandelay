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

package com.spotify.api.vandelay.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class Base64EncodedValueTest {

  @Test
  public void testProperties() {
    final byte[] content = "mydata".getBytes(StandardCharsets.UTF_8);
    final Base64EncodedValue testValue = new Base64EncodedValue("test", content);

    assertEquals("test", testValue.getName());
    assertEquals("bXlkYXRh", testValue.getBase64Content());
  }

  @Test
  public void testEncodeDecode() {
    final byte[] content = "mydata".getBytes(StandardCharsets.UTF_8);
    final Base64EncodedValue testValue = new Base64EncodedValue("test", content);

    assertTrue(Arrays.equals(content, testValue.toDecodedBytes()));
  }
}
