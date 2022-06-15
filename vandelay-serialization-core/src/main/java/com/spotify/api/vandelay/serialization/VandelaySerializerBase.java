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

package com.spotify.api.vandelay.serialization;

import com.spotify.api.vandelay.core.meta.RequiredField;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class VandelaySerializerBase<T> implements VandelaySerializer<T> {

  @Override
  public String serialize(final T value) throws VandelaySerializationException {
    validateRequiredFields(value);
    return serializeImpl(value);
  }

  @Override
  public T deserialize(final String content, final Class<T> clazz)
      throws VandelaySerializationException {
    final T value = deserializeImpl(content, clazz);
    validateRequiredFields(value);
    return value;
  }

  protected void validateRequiredFields(final T value) {
    final Class<?> clazz = value.getClass();
    try {
      List<Field> fields = getAllFields(clazz);
      for (final var field : fields) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(RequiredField.class) && Objects.isNull(field.get(value))) {
          throw new VandelaySerializationException("Field " + field.getName() + " cannot be null.");
        }
      }
    } catch (final IllegalAccessException ex) {
      throw new VandelaySerializationException(ex.getMessage());
    }
  }

  private List<Field> getAllFields(final Class<?> type) {
    List<Field> fields = new ArrayList<>();
    for (var clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    }
    return fields;
  }

  protected abstract String serializeImpl(T configuration) throws VandelaySerializationException;

  protected abstract T deserializeImpl(String content, Class<T> clazz)
      throws VandelaySerializationException;
}
