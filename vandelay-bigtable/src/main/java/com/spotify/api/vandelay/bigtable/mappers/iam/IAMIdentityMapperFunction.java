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

package com.spotify.api.vandelay.bigtable.mappers.iam;

import com.google.cloud.Identity;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMIdentity;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAMIdentityMapperFunction extends VandelayObjectMapperFunction<Identity, IAMIdentity> {

  private static final Logger logger = LoggerFactory.getLogger(IAMConditionMapperFunction.class);

  public IAMIdentityMapperFunction() {
    super(Identity.class, IAMIdentity.class);
  }

  @Override
  protected Function<Identity, Optional<IAMIdentity>> convertToImpl() {
    return identity ->
        Optional.of(new IAMIdentity(identity.getValue(), identity.getType().toString()));
  }

  @Override
  protected Function<IAMIdentity, Optional<Identity>> convertFromImpl() {
    return iamIdentity -> Optional.of(Identity.valueOf(iamIdentity.getValue()));
  }
}
