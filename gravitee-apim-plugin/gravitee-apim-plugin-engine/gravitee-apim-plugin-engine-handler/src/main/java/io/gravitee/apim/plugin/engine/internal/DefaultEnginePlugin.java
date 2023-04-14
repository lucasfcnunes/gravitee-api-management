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

import io.gravitee.apim.plugin.engine.EnginePlugin;
import io.gravitee.gateway.reactive.api.engine.EngineConfiguration;
import io.gravitee.gateway.reactive.api.engine.EngineFactory;
import io.gravitee.plugin.core.api.Plugin;
import io.gravitee.plugin.core.api.PluginManifest;
import java.net.URL;
import java.nio.file.Path;
import lombok.AllArgsConstructor;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@AllArgsConstructor
public class DefaultEnginePlugin<F extends EngineFactory, C extends EngineConfiguration> implements EnginePlugin<F, C> {

    private final Plugin plugin;
    private final Class<F> engineFactoryClass;
    private final Class<C> engineConfigurationClass;

    @Override
    public Class<F> engineFactory() {
        return engineFactoryClass;
    }

    @Override
    public String clazz() {
        return plugin.clazz();
    }

    @Override
    public URL[] dependencies() {
        return plugin.dependencies();
    }

    @Override
    public String id() {
        return plugin.id();
    }

    @Override
    public PluginManifest manifest() {
        return plugin.manifest();
    }

    @Override
    public Path path() {
        return plugin.path();
    }

    @Override
    public Class<C> configuration() {
        return engineConfigurationClass;
    }
}
