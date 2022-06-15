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

import com.google.cloud.Condition;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMCondition;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAMConditionMapperFunction
    extends VandelayObjectMapperFunction<Condition, IAMCondition> {

  private static final Logger logger = LoggerFactory.getLogger(IAMConditionMapperFunction.class);

  public IAMConditionMapperFunction() {
    super(Condition.class, IAMCondition.class);
  }

  @Override
  protected Function<Condition, Optional<IAMCondition>> convertToImpl() {
    return condition -> {
      if (condition == null) {
        return Optional.empty();
      }

      return Optional.of(
          new IAMCondition(
              condition.getTitle(), condition.getDescription(), condition.getExpression()));
    };
  }

  @Override
  protected Function<IAMCondition, Optional<Condition>> convertFromImpl() {
    return iamCondition -> {
      if (iamCondition == null) {
        return Optional.empty();
      }
      return Optional.of(
          Condition.newBuilder()
              .setTitle(iamCondition.getTitle())
              .setDescription(iamCondition.getDescription())
              .setExpression(iamCondition.getExpression())
              .build());
    };
  }
}
