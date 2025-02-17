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
package io.gravitee.rest.api.management.rest.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.rest.api.model.ApiKeyEntity;
import io.gravitee.rest.api.model.ApiKeyMode;
import io.gravitee.rest.api.model.ApplicationEntity;
import io.gravitee.rest.api.model.SubscriptionEntity;
import io.gravitee.rest.api.service.common.GraviteeContext;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * @author GraviteeSource Team
 */
public class ApiSubscriptionApikeysResourceTest extends AbstractResourceTest {

    private static final String API_ID = "my-api";
    private static final String SUBSCRIPTION_ID = "my-subscription";
    private static final String APPLICATION_ID = "my-application";

    @Override
    protected String contextPath() {
        return "apis/" + API_ID + "/subscriptions/" + SUBSCRIPTION_ID + "/apikeys";
    }

    @Before
    public void setUp() {
        reset(apiKeyService);
    }

    @Test
    public void get_should_return_apikeys_list_from_service_with_http_200() {
        ApiKeyEntity apiKeyFromService = new ApiKeyEntity();
        apiKeyFromService.setId("test-id");
        List<ApiKeyEntity> apiKeysFromService = List.of(apiKeyFromService);
        when(apiKeyService.findBySubscription(GraviteeContext.getExecutionContext(), SUBSCRIPTION_ID)).thenReturn(apiKeysFromService);

        Response response = envTarget().request().get();

        assertEquals(HttpStatusCode.OK_200, response.getStatus());
        assertEquals(apiKeysFromService, response.readEntity(new GenericType<List<ApiKeyEntity>>() {}));
    }

    @Test
    public void post_on_renew_should_return_renew_apikeys_and_return_http_201_with_location_header() {
        mockExistingSubscriptionWithApplication(ApiKeyMode.EXCLUSIVE);

        ApiKeyEntity renewedApiKey = new ApiKeyEntity();
        renewedApiKey.setId("test-id");

        when(apiKeyService.renew(eq(GraviteeContext.getExecutionContext()), any(), any())).thenReturn(renewedApiKey);

        Response response = envTarget("/_renew").request().post(null);

        assertEquals(HttpStatusCode.CREATED_201, response.getStatus());
        assertEquals(renewedApiKey, response.readEntity(ApiKeyEntity.class));
        assertTrue(response.getLocation().toString().endsWith("/apis/my-api/subscriptions/my-subscription/apikeys/test-id"));
    }

    @Test
    public void post_on_renew_should_return_400_when_shared_api_key_mode() {
        mockExistingSubscriptionWithApplication(ApiKeyMode.SHARED);

        Response response = envTarget("/_renew").request().post(null);

        assertEquals(HttpStatusCode.BAD_REQUEST_400, response.getStatus());
    }

    private void mockExistingSubscriptionWithApplication(ApiKeyMode apiKeyMode) {
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setId("test-subscription-id");
        subscription.setApplication(APPLICATION_ID);
        when(subscriptionService.findById(SUBSCRIPTION_ID)).thenReturn(subscription);

        ApplicationEntity application = new ApplicationEntity();
        application.setApiKeyMode(apiKeyMode);
        when(applicationService.findById(GraviteeContext.getExecutionContext(), APPLICATION_ID)).thenReturn(application);
    }
}
