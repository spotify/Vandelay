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

import java.util.Optional;

public class Status {

  private final boolean succeeded;
  private final String message;
  private final Object metadata;

  private Status(final boolean succeeded) {
    this(succeeded, null);
  }

  private Status(final boolean succeeded, final String message) {
    this(succeeded, message, null);
  }

  private Status(final boolean succeeded, final String message, final Object metadata) {
    this.succeeded = succeeded;
    this.message = message;
    this.metadata = metadata;
  }

  public static Status success() {
    return new Status(true);
  }

  public static Status success(final String message) {
    return success(message, Optional.empty());
  }

  public static Status success(final String message, final Object metadata) {
    return new Status(true, message, metadata);
  }

  public static Status fail() {
    return new Status(false);
  }

  public static Status fail(final String message) {
    return fail(message, Optional.empty());
  }

  public static Status fail(final String message, final Object metadata) {
    return new Status(false, message, metadata);
  }

  public boolean succeeded() {
    return succeeded;
  }

  public boolean failed() {
    return !succeeded();
  }

  public Optional<String> getMessage() {
    return Optional.ofNullable(message);
  }

  public Optional<Object> getMetadata() {
    return Optional.ofNullable(metadata);
  }
}
