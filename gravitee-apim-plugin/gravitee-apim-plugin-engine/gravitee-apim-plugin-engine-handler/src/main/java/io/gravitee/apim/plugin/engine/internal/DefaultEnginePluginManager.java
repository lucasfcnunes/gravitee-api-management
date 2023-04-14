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

import io.gravitee.apim.plugin.engine.EngineClassLoaderFactory;
import io.gravitee.apim.plugin.engine.EnginePlugin;
import io.gravitee.apim.plugin.engine.EnginePluginManager;
import io.gravitee.gateway.reactive.api.engine.EngineFactory;
import io.gravitee.gateway.reactive.api.helper.PluginConfigurationHelper;
import io.gravitee.plugin.core.api.AbstractConfigurablePluginManager;
import io.gravitee.plugin.core.api.PluginClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@AllArgsConstructor
@Slf4j
public class DefaultEnginePluginManager extends AbstractConfigurablePluginManager<EnginePlugin<?, ?>> implements EnginePluginManager {

    private final EngineClassLoaderFactory classLoaderFactory;
    private final PluginConfigurationHelper pluginConfigurationHelper;
    private final Map<String, EngineFactory<?>> factories = new HashMap<>();

    @Override
    public void register(EnginePlugin<?, ?> plugin) {
        super.register(plugin);

        // Create engine
        PluginClassLoader pluginClassLoader = classLoaderFactory.getOrCreateClassLoader(plugin);
        try {
            final Class<EngineFactory<?>> engineFactoryClass = (Class<EngineFactory<?>>) pluginClassLoader.loadClass(plugin.clazz());
            EngineFactory<?> factory = createFactory(engineFactoryClass);
            factories.put(plugin.id(), factory);
        } catch (Exception e) {
            log.error("Unexpected error while loading engine plugin: {}", plugin.clazz(), e);
        }
    }

    @Override
    public EngineFactory<?> getFactoryById(String enginePluginId) {
        return factories.get(enginePluginId);
    }

    @Override
    public List<?> getAllFactories() {
        return new ArrayList<>(factories.values());
    }

    private EngineFactory<?> createFactory(final Class<EngineFactory<?>> engineFactoryClass)
        throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        EngineFactory<?> factory;
        try {
            final Constructor<EngineFactory<?>> constructorWithFactoryHelper = engineFactoryClass.getDeclaredConstructor(
                PluginConfigurationHelper.class
            );
            factory = constructorWithFactoryHelper.newInstance(pluginConfigurationHelper);
        } catch (NoSuchMethodException e) {
            final Constructor<EngineFactory<?>> constructorWithoutFactoryHelper = engineFactoryClass.getDeclaredConstructor();
            factory = constructorWithoutFactoryHelper.newInstance();
        }
        return factory;
    }
}
