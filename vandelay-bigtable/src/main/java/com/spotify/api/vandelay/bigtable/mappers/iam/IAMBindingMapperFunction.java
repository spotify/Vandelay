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

import com.google.cloud.Binding;
import com.google.cloud.Condition;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMBinding;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMCondition;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAMBindingMapperFunction extends VandelayObjectMapperFunction<Binding, IAMBinding> {

  private static final Logger logger = LoggerFactory.getLogger(IAMBindingMapperFunction.class);

  public IAMBindingMapperFunction() {
    super(Binding.class, IAMBinding.class);
  }

  @Override
  protected Function<Binding, Optional<IAMBinding>> convertToImpl() {
    return binding -> {
      final IAMConditionMapperFunction conditionMapperFunction = new IAMConditionMapperFunction();
      final Optional<IAMCondition> condition =
          conditionMapperFunction.convertTo(binding.getCondition());

      return Optional.of(
          new IAMBinding(binding.getRole(), binding.getMembers(), condition.orElse(null)));
    };
  }

  @Override
  protected Function<IAMBinding, Optional<Binding>> convertFromImpl() {
    return iamBinding -> {
      final Condition condition =
          new IAMConditionMapperFunction()
              .convertFromImpl()
              .apply(iamBinding.getCondition())
              .orElse(null);
      final Binding.Builder binding =
          Binding.newBuilder().setRole(iamBinding.getRole()).setMembers(iamBinding.getMembers());
      if (Objects.nonNull(condition)) {
        binding.setCondition(condition);
      }
      return Optional.of(binding.build());
    };
  }
}
