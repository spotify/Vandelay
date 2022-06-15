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

package com.spotify.api.vandelay.core.util;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

public class Reflected {

  private Reflected() {}

  public static <T> Optional<T> getField(
      final Object targetObj,
      final Class<?> targetObjClass,
      final String fieldName,
      final Class<?> fieldClass) {
    try {
      final var handle =
          MethodHandles.privateLookupIn(targetObjClass, MethodHandles.lookup())
              .findVarHandle(targetObjClass, fieldName, fieldClass);
      return Optional.of((T) handle.get(targetObj));
    } catch (IllegalAccessException | NoSuchFieldException ex) {
      return Optional.empty();
    }
  }
}
