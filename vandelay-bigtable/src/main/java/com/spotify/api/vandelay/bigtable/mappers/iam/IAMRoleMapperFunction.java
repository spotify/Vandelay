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

import com.google.cloud.Role;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMRole;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAMRoleMapperFunction extends VandelayObjectMapperFunction<Role, IAMRole> {

  private static final Logger logger = LoggerFactory.getLogger(IAMRoleMapperFunction.class);

  public IAMRoleMapperFunction() {
    super(Role.class, IAMRole.class);
  }

  @Override
  protected Function<Role, Optional<IAMRole>> convertToImpl() {
    return role -> Optional.of(new IAMRole(role.getValue()));
  }

  @Override
  protected Function<IAMRole, Optional<Role>> convertFromImpl() {
    return iamRole -> Optional.of(Role.of(iamRole.getValue()));
  }
}
