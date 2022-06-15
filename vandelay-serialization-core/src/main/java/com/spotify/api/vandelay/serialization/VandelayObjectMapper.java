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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class VandelayObjectMapper {

  private static final Set<VandelayObjectMapperFunction<?, ?>> mappers = new HashSet<>();

  private VandelayObjectMapper() {}

  public static boolean add(final VandelayObjectMapperFunction<?, ?> mapper) {
    if (exists(mapper)) {
      return false;
    }

    mappers.add(mapper);
    return true;
  }

  public static boolean remove(final VandelayObjectMapperFunction<?, ?> mapper) {
    if (!exists(mapper)) {
      return false;
    }

    mappers.remove(mapper);
    return true;
  }

  public static Optional<VandelayObjectMapperFunction<?, ?>> get(
      final Class<?> from, final Class<?> to) {
    return mappers.stream().filter(x -> x.toType.equals(to) && x.fromType.equals(from)).findFirst();
  }

  public static boolean exists(final VandelayObjectMapperFunction<?, ?> mapper) {
    return mappers.contains(mapper);
  }
}
