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

package com.spotify.api.vandelay.serialization.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.spotify.api.vandelay.serialization.VandelaySerializationException;
import com.spotify.api.vandelay.serialization.VandelaySerializerBase;

public class VandelayJacksonSerializer<T> extends VandelaySerializerBase<T> {

  private static final ObjectMapper objectMapper =
      new ObjectMapper()
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .registerModule(new Jdk8Module());

  @Override
  protected String serializeImpl(final T value) throws VandelaySerializationException {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new VandelaySerializationException(ex.getMessage());
    }
  }

  @Override
  protected T deserializeImpl(final String content, final Class<T> clazz)
      throws VandelaySerializationException {
    try {
      return objectMapper.readValue(content, clazz);
    } catch (JsonProcessingException ex) {
      throw new VandelaySerializationException(ex.getMessage());
    }
  }
}
