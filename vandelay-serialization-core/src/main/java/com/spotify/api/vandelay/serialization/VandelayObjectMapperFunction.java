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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class VandelayObjectMapperFunction<TLeft, TRight> {

  protected final Class<?> fromType;
  protected final Class<?> toType;

  public VandelayObjectMapperFunction(final Class<?> fromType, final Class<?> toType) {
    this.fromType = fromType;
    this.toType = toType;
  }

  public Optional<TRight> convertTo(final TLeft from) {
    return convertToImpl().apply(from);
  }

  public Optional<TLeft> convertFrom(final TRight to) {
    return convertFromImpl().apply(to);
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof VandelayObjectMapperFunction)) {
      return false;
    }
    final VandelayObjectMapperFunction other = (VandelayObjectMapperFunction) o;

    return other.toType.equals(this.toType) && other.fromType.equals(this.fromType);
  }

  public int hashCode() {
    return Objects.hash(toType, fromType);
  }

  protected abstract Function<TLeft, Optional<TRight>> convertToImpl();

  protected abstract Function<TRight, Optional<TLeft>> convertFromImpl();
}
