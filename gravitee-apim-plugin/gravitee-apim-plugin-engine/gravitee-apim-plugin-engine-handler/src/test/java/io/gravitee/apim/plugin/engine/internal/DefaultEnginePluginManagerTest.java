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

import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.apim.plugin.engine.EnginePluginManager;
import io.gravitee.apim.plugin.engine.internal.fake.FakeEngineFactory;
import io.gravitee.apim.plugin.engine.internal.fake.FakeEnginePlugin;
import io.gravitee.gateway.reactive.api.engine.Engine;
import io.gravitee.gateway.reactive.api.engine.EngineConfiguration;
import io.gravitee.gateway.reactive.api.engine.EngineFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DefaultEnginePluginManagerTest {

    private EnginePluginManager cut;

    @BeforeEach
    void setUp() {
        cut = new DefaultEnginePluginManager(new DefaultEngineClassLoaderFactory(), null);
    }

    @Test
    void should_register_new_engine_plugin() {
        final DefaultEnginePlugin<FakeEngineFactory, EngineConfiguration> enginePlugin = new DefaultEnginePlugin<>(
            new FakeEnginePlugin(),
            FakeEngineFactory.class,
            null
        );

        cut.register(enginePlugin);

        final EngineFactory<?> fakeEngineFactory = cut.getFactoryById("fake-engine");
        assertThat(fakeEngineFactory).isNotNull();

        assertThat(cut.getAllFactories()).hasSize(1).contains(fakeEngineFactory);

        final Engine fakeEngine = fakeEngineFactory.createEngine();
        assertThat(fakeEngine).isNotNull();
    }

    @Test
    void should_not_retrieve_unregistered_plugin() {
        final EngineFactory<?> unregistered = cut.getFactoryById("unregistered");
        assertThat(unregistered).isNull();
    }
}
