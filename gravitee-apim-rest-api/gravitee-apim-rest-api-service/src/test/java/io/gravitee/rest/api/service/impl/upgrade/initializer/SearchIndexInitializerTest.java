/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.service.impl.upgrade.initializer;

import static io.gravitee.repository.management.model.UserStatus.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import io.gravitee.common.data.domain.Page;
import io.gravitee.repository.management.api.ApiRepository;
import io.gravitee.repository.management.api.EnvironmentRepository;
import io.gravitee.repository.management.api.UserRepository;
import io.gravitee.repository.management.api.search.ApiCriteria;
import io.gravitee.repository.management.api.search.ApiFieldFilter;
import io.gravitee.repository.management.model.Api;
import io.gravitee.repository.management.model.Environment;
import io.gravitee.repository.management.model.User;
import io.gravitee.rest.api.model.PrimaryOwnerEntity;
import io.gravitee.rest.api.model.UserEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.search.Indexable;
import io.gravitee.rest.api.service.PageService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.converter.ApiConverter;
import io.gravitee.rest.api.service.converter.UserConverter;
import io.gravitee.rest.api.service.exceptions.PrimaryOwnerNotFoundException;
import io.gravitee.rest.api.service.search.SearchEngineService;
import io.gravitee.rest.api.service.v4.PrimaryOwnerService;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchIndexInitializerTest {

    @InjectMocks
    private SearchIndexInitializer initializer;

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private PageService pageService;

    @Mock
    private PrimaryOwnerService primaryOwnerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchEngineService searchEngineService;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private ApiConverter apiConverter;

    @Mock
    private UserConverter userConverter;

    private final PrimaryOwnerEntity primaryOwnerEntity = new PrimaryOwnerEntity();

    @Before
    public void setup() throws Exception {
        mockEnvironment("env1", "org1");
        mockEnvironment("env2", "org2");
        mockEnvironment("env3", "org1");
        when(primaryOwnerService.getPrimaryOwner(any(), any())).thenReturn(primaryOwnerEntity);
    }

    @Test
    public void runApisIndexationAsync_should_retrieve_environment_of_each_api() throws Exception {
        mockTestApis();
        mockTestUsers();

        List<CompletableFuture<?>> futures = initializer.runApisIndexationAsync(Executors.newSingleThreadExecutor());
        assertEquals(futures.size(), 4);

        futures.forEach(CompletableFuture::join);

        verify(environmentRepository, times(1)).findById("env1");
        verify(environmentRepository, times(1)).findById("env2");
        verify(environmentRepository, times(1)).findById("env3");
        verifyNoMoreInteractions(environmentRepository);
    }

    @Test
    public void runApisIndexationAsync_should_index_every_api() throws Exception {
        mockTestApis();
        mockTestUsers();

        List<CompletableFuture<?>> futures = initializer.runApisIndexationAsync(Executors.newSingleThreadExecutor());
        assertEquals(futures.size(), 4);

        futures.forEach(CompletableFuture::join);

        verify(searchEngineService, times(1))
            .index(
                argThat(e -> e.hasEnvironmentId() && e.getEnvironmentId().equals("env1") && e.getOrganizationId().equals("org1")),
                argThat(api -> api.getId().equals("api1")),
                eq(true),
                eq(false)
            );
        verify(searchEngineService, times(1))
            .index(
                argThat(e -> e.hasEnvironmentId() && e.getEnvironmentId().equals("env2") && e.getOrganizationId().equals("org2")),
                argThat(api -> api.getId().equals("api2")),
                eq(true),
                eq(false)
            );
        verify(searchEngineService, times(1))
            .index(
                argThat(e -> e.hasEnvironmentId() && e.getEnvironmentId().equals("env1") && e.getOrganizationId().equals("org1")),
                argThat(api -> api.getId().equals("api3")),
                eq(true),
                eq(false)
            );
        verify(searchEngineService, times(1))
            .index(
                argThat(e -> e.hasEnvironmentId() && e.getEnvironmentId().equals("env3") && e.getOrganizationId().equals("org1")),
                argThat(api -> api.getId().equals("api4")),
                eq(true),
                eq(false)
            );
    }

    @Test
    public void runApisIndexationAsync_should_index_every_api_even_if_primaryOwner_not_found() throws Exception {
        mockTestApis();
        mockTestUsers();
        when(primaryOwnerService.getPrimaryOwner(any(), any())).thenThrow(PrimaryOwnerNotFoundException.class);

        List<CompletableFuture<?>> futures = initializer.runApisIndexationAsync(Executors.newSingleThreadExecutor());
        assertEquals(futures.size(), 4);

        futures.forEach(CompletableFuture::join);

        verify(searchEngineService, times(4)).index(any(ExecutionContext.class), any(Indexable.class), anyBoolean(), anyBoolean());
    }

    @Test
    public void runUsersIndexationAsync_should_index_every_user() throws Exception {
        mockTestApis();
        mockTestUsers();

        List<CompletableFuture<?>> futures = initializer.runUsersIndexationAsync(Executors.newSingleThreadExecutor());
        assertEquals(futures.size(), 4);

        futures.forEach(CompletableFuture::join);

        verify(searchEngineService, times(1))
            .index(
                argThat(e -> !e.hasEnvironmentId() && e.getOrganizationId().equals("org1")),
                argThat(user -> user.getId().equals("user1")),
                eq(true),
                eq(false)
            );
        verify(searchEngineService, times(1))
            .index(
                argThat(e -> !e.hasEnvironmentId() && e.getOrganizationId().equals("org2")),
                argThat(user -> user.getId().equals("user2")),
                eq(true),
                eq(false)
            );
        verify(searchEngineService, times(1))
            .index(
                argThat(e -> !e.hasEnvironmentId() && e.getOrganizationId().equals("org1")),
                argThat(user -> user.getId().equals("user3")),
                eq(true),
                eq(false)
            );
        verify(searchEngineService, times(1))
            .index(
                argThat(e -> !e.hasEnvironmentId() && e.getOrganizationId().equals("org3")),
                argThat(user -> user.getId().equals("user4")),
                eq(true),
                eq(false)
            );
    }

    @Test
    public void testOrder() {
        assertEquals(InitializerOrder.SEARCH_INDEX_INITIALIZER, initializer.getOrder());
    }

    private void mockTestApis() throws Exception {
        Stream<Api> apis = Stream.of(
            mockTestApi("api1", "env1"),
            mockTestApi("api2", "env2"),
            mockTestApi("api3", "env1"),
            mockTestApi("api4", "env3")
        );
        when(apiRepository.search(any(ApiCriteria.class), eq(null), any(ApiFieldFilter.class))).thenReturn(apis);
    }

    private Api mockTestApi(String apiId, String environmentId) {
        Api api = new Api();
        api.setId(apiId);
        api.setEnvironmentId(environmentId);

        ApiEntity apiEntity = new ApiEntity();
        apiEntity.setId(apiId);

        when(apiConverter.toApiEntity(same(api), any())).thenReturn(apiEntity);

        return api;
    }

    private void mockTestUsers() throws Exception {
        List<User> users = List.of(
            mockTestUser("user1", "org1"),
            mockTestUser("user2", "org2"),
            mockTestUser("user3", "org1"),
            mockTestUser("user4", "org3")
        );
        Page<User> page = new Page<>(users, 0, users.size(), users.size());
        when(userRepository.search(argThat(criteria -> criteria.getStatuses().length == 1 && criteria.getStatuses()[0] == ACTIVE), any()))
            .thenReturn(page);
    }

    private User mockTestUser(String userId, String organizationId) {
        User user = new User();
        user.setId(userId);
        user.setOrganizationId(organizationId);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        when(userConverter.toUserEntity(user)).thenReturn(userEntity);

        return user;
    }

    private void mockEnvironment(String envId, String orgId) throws Exception {
        Environment environment1 = new Environment();
        environment1.setId(envId);
        environment1.setOrganizationId(orgId);
        when(environmentRepository.findById(envId)).thenReturn(Optional.of(environment1));
    }
}
