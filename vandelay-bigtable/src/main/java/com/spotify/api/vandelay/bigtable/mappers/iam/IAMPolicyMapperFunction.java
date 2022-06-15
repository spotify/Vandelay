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
import com.google.cloud.Policy;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMBinding;
import com.spotify.api.vandelay.bigtable.model.gcp.iam.IAMPolicy;
import com.spotify.api.vandelay.serialization.VandelayObjectMapperFunction;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAMPolicyMapperFunction extends VandelayObjectMapperFunction<Policy, IAMPolicy> {

  private static final Logger logger = LoggerFactory.getLogger(IAMBindingMapperFunction.class);

  public IAMPolicyMapperFunction() {
    super(Policy.class, IAMPolicy.class);
  }

  @Override
  protected Function<Policy, Optional<IAMPolicy>> convertToImpl() {
    return policy -> {
      final IAMBindingMapperFunction bindingsConverter = new IAMBindingMapperFunction();
      final List<IAMBinding> bindings =
          policy.getBindingsList().stream()
              .map(
                  x -> {
                    final Optional<IAMBinding> binding = bindingsConverter.convertTo(x);
                    if (binding.isEmpty()) {
                      logger.error("IAMBinding could not be converted");
                      return null;
                    }

                    return binding.get();
                  })
              .filter(Objects::nonNull)
              .toList();
      return Optional.of(new IAMPolicy(policy.getEtag(), policy.getVersion(), bindings));
    };
  }

  @Override
  protected Function<IAMPolicy, Optional<Policy>> convertFromImpl() {
    return iamPolicy -> {
      List<Binding> bindings =
          iamPolicy.getBindingList().stream()
              .map(x -> new IAMBindingMapperFunction().convertFromImpl().apply(x))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .toList();
      return Optional.of(
          Policy.newBuilder()
              .setEtag(iamPolicy.getEtag())
              .setVersion(iamPolicy.getVersion())
              .setBindings(bindings)
              .build());
    };
  }
}
