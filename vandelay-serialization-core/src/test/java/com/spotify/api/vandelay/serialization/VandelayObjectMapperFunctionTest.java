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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

public class VandelayObjectMapperFunctionTest {

  @Test
  public void testObjectConvert() {

    final VandelayObjectMapperFunction<Double, Integer> test =
        new VandelayObjectMapperFunction<>(Integer.class, Double.class) {

          @Override
          protected Function<Double, Optional<Integer>> convertToImpl() {
            return aDouble -> Optional.of((int) aDouble.doubleValue());
          }

          @Override
          protected Function<Integer, Optional<Double>> convertFromImpl() {
            return aInt -> Optional.of((double) aInt + 0.1);
          }
        };

    assertEquals(5, test.convertTo(5.5).get());
    assertEquals(4.1, test.convertFrom(4).get());
  }

  @Test
  public void testConverterEquality() {

    final VandelayObjectMapperFunction<Double, Integer> test =
        new VandelayObjectMapperFunction<>(Integer.class, Double.class) {

          @Override
          protected Function<Double, Optional<Integer>> convertToImpl() {
            return aDouble -> Optional.of((int) aDouble.doubleValue());
          }

          @Override
          protected Function<Integer, Optional<Double>> convertFromImpl() {
            return aInt -> Optional.of((double) aInt + 0.1);
          }
        };
    final VandelayObjectMapperFunction<Double, Integer> test2 =
        new VandelayObjectMapperFunction<>(Integer.class, Double.class) {

          @Override
          protected Function<Double, Optional<Integer>> convertToImpl() {
            return aDouble -> Optional.of((int) aDouble.doubleValue());
          }

          @Override
          protected Function<Integer, Optional<Double>> convertFromImpl() {
            return aInt -> Optional.of((double) aInt + 0.1);
          }
        };

    assertEquals(test, test2);
  }

  @Test
  public void testConverterHashing() {

    final VandelayObjectMapperFunction<Double, Integer> test =
        new VandelayObjectMapperFunction<>(Integer.class, Double.class) {

          @Override
          protected Function<Double, Optional<Integer>> convertToImpl() {
            return aDouble -> Optional.of((int) aDouble.doubleValue());
          }

          @Override
          protected Function<Integer, Optional<Double>> convertFromImpl() {
            return aInt -> Optional.of((double) aInt + 0.1);
          }
        };
    final VandelayObjectMapperFunction<Double, Integer> test2 =
        new VandelayObjectMapperFunction<>(Integer.class, Double.class) {

          @Override
          protected Function<Double, Optional<Integer>> convertToImpl() {
            return aDouble -> Optional.of((int) aDouble.doubleValue());
          }

          @Override
          protected Function<Integer, Optional<Double>> convertFromImpl() {
            return aInt -> Optional.of((double) aInt + 0.1);
          }
        };

    final Set<VandelayObjectMapperFunction<Double, Integer>> convertSet = new HashSet<>();
    convertSet.add(test);
    convertSet.add(test2);

    assertEquals(1, convertSet.size());
  }
}
