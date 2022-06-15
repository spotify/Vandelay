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

package com.spotify.api.vandelay.core.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VandelayEventListenerTest {

  public static class TestEventPublisher implements VandelayEventPublisher<Integer> {

    private final List<VandelayEventListener<Integer>> observers = new ArrayList<>();

    @Override
    public void addListener(final VandelayEventListener<Integer> observer) {
      observers.add(observer);
    }

    @Override
    public void removeListener(final VandelayEventListener<Integer> observer) {
      observers.remove(observer);
    }

    @Override
    public void notifyListeners(Integer event) {
      observers.forEach(x -> x.onEvent(event));
    }
  }

  public static class TestEventListener implements VandelayEventListener<Integer> {

    private int i = 1869375329;

    public int get() {
      return i;
    }

    @Override
    public void onEvent(Integer event) {
      this.i = event;
    }
  }

  @Test
  public void testEventListener() {
    TestEventPublisher generator = new TestEventPublisher();
    TestEventListener listener = new TestEventListener();

    generator.addListener(listener);
    generator.notifyListeners(123);

    assertEquals(123, listener.get());
  }
}
