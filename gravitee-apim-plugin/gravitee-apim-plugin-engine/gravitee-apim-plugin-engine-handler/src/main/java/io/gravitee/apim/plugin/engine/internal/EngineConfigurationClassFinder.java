/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.apim.plugin.engine.internal;

import io.gravitee.gateway.reactive.api.engine.EngineConfiguration;
import io.gravitee.plugin.core.api.AbstractSingleSubTypesFinder;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class EngineConfigurationClassFinder extends AbstractSingleSubTypesFinder<EngineConfiguration> {

    public EngineConfigurationClassFinder() {
        super(EngineConfiguration.class);
    }

    @Override
    public Collection<Class<? extends EngineConfiguration>> lookup(Class<?> clazz, ClassLoader classLoader) {
        log.debug("Looking for a configuration class for engine {} in package {}", clazz.getName(), clazz.getPackage().getName());
        Collection<Class<? extends EngineConfiguration>> configurations = super.lookup(clazz, classLoader);

        if (configurations.isEmpty()) {
            log.info("No configuration class defined for engine {}", clazz.getName());
        }

        return configurations;
    }
}
