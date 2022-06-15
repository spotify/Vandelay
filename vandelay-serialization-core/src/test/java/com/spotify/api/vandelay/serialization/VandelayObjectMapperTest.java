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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class VandelayObjectMapperTest {

  private final VandelayObjectMapperFunction<Double, Integer> testMapper =
      new VandelayObjectMapperFunction<>(Double.class, Integer.class) {

        @Override
        protected Function<Double, Optional<Integer>> convertToImpl() {
          return aDouble -> Optional.of((int) aDouble.doubleValue());
        }

        @Override
        protected Function<Integer, Optional<Double>> convertFromImpl() {
          return aInt -> Optional.of((double) aInt + 0.1);
        }
      };

  @Test
  public void testLifecycle() {

    assertTrue(VandelayObjectMapper.add(testMapper));
    assertTrue(VandelayObjectMapper.exists(testMapper));
    assertTrue(VandelayObjectMapper.get(Double.class, Integer.class).isPresent());
    assertTrue(VandelayObjectMapper.remove(testMapper));
    assertFalse(VandelayObjectMapper.exists(testMapper));
  }
}
