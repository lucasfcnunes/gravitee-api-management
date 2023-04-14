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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.gravitee.apim.plugin.engine.EnginePlugin;
import io.gravitee.apim.plugin.engine.EnginePluginManager;
import io.gravitee.apim.plugin.engine.internal.fake.FakeEngineFactory;
import io.gravitee.apim.plugin.engine.internal.fake.FakeEnginePlugin;
import io.gravitee.plugin.core.api.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EnginePluginHandlerTest {

    @Mock
    private EnginePluginManager enginePluginManager;

    private EnginePluginHandler cut;

    @BeforeEach
    void setUp() {
        cut = new EnginePluginHandler(enginePluginManager);
    }

    @Test
    void should_be_engine_type() {
        assertThat(cut.type()).isEqualTo(EnginePlugin.PLUGIN_TYPE);
    }

    @Test
    void should_handle_engine() {
        assertThat(cut.canHandle(new FakeEnginePlugin())).isTrue();
    }

    @Test
    void should_not_handle_engine() {
        Plugin mockPlugin = mock(Plugin.class);
        when(mockPlugin.type()).thenReturn("wrong");
        assertThat(cut.canHandle(mockPlugin)).isFalse();
    }

    @Test
    void should_handle_new_plugin() {
        final FakeEnginePlugin fakeEnginePlugin = new FakeEnginePlugin();
        cut.handle(fakeEnginePlugin, FakeEngineFactory.class);
        verify(enginePluginManager).register(any(EnginePlugin.class));
    }
}
