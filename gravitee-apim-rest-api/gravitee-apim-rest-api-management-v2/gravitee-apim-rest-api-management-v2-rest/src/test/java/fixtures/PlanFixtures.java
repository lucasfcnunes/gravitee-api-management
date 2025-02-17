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
package fixtures;

import io.gravitee.definition.model.v4.plan.PlanSecurity;
import io.gravitee.definition.model.v4.plan.PlanStatus;
import io.gravitee.rest.api.management.v2.rest.model.*;
import io.gravitee.rest.api.model.v4.plan.PlanEntity;
import io.gravitee.rest.api.model.v4.plan.PlanSecurityType;
import io.gravitee.rest.api.model.v4.plan.PlanType;
import io.gravitee.rest.api.model.v4.plan.PlanValidationType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PlanFixtures {

    private static final PlanSecurity.PlanSecurityBuilder BASE_PLAN_SECURITY_V4 = PlanSecurity
        .builder()
        .type(PlanSecurityType.API_KEY.getLabel())
        .configuration("{\"nice\": \"config\"}");

    protected static final io.gravitee.rest.api.management.v2.rest.model.PlanSecurity.PlanSecurityBuilder<?, ?> BASE_PLAN_SECURITY =
        io.gravitee.rest.api.management.v2.rest.model.PlanSecurity
            .builder()
            .type(io.gravitee.rest.api.management.v2.rest.model.PlanSecurityType.API_KEY)
            .configuration("{\"nice\": \"config\"}");

    private static final UpdateGenericPlanSecurity.UpdateGenericPlanSecurityBuilder<?, ?> BASE_UPDATE_PLAN_SECURITY =
        UpdateGenericPlanSecurity.builder().configuration("{\"nice\": \"config\"}");

    private static final PlanEntity.PlanEntityBuilder BASE_PLAN_ENTITY_V4 = PlanEntity
        .builder()
        .id("my-plan")
        .apiId("my-api")
        .name("My plan")
        .description("Description")
        .order(1)
        .characteristics(List.of("characteristic1", "characteristic2"))
        .createdAt(new Date())
        .updatedAt(new Date())
        .commentMessage("Comment message")
        .crossId("my-plan-crossId")
        .generalConditions("General conditions")
        .tags(Set.of("tag1", "tag2"))
        .status(PlanStatus.PUBLISHED)
        .security(BASE_PLAN_SECURITY_V4.build())
        .type(PlanType.API)
        .excludedGroups(List.of("excludedGroup1", "excludedGroup2"))
        .validation(PlanValidationType.AUTO)
        .selectionRule("{#request.attribute['selectionRule'] != null}")
        .flows(List.of(FlowFixtures.aModelFlowV4()));

    private static final CreatePlanV4.CreatePlanV4Builder BASE_CREATE_PLAN_V4 = CreatePlanV4
        .builder()
        .name("My plan")
        .description("Description")
        .order(1)
        .characteristics(List.of("characteristic1", "characteristic2"))
        .commentMessage("Comment message")
        .crossId("my-plan-crossId")
        .generalConditions("General conditions")
        .tags(List.of("tag1", "tag2"))
        .excludedGroups(List.of("excludedGroup1", "excludedGroup2"))
        .validation(PlanValidation.AUTO)
        .selectionRule("{#request.attribute['selectionRule'] != null}")
        .security(BASE_PLAN_SECURITY.build())
        .flows(List.of(FlowFixtures.aFlowV4()));

    private static final CreatePlanV2.CreatePlanV2Builder BASE_CREATE_PLAN_V2 = CreatePlanV2
        .builder()
        .name("My plan")
        .description("Description")
        .order(1)
        .characteristics(List.of("characteristic1", "characteristic2"))
        .commentMessage("Comment message")
        .crossId("my-plan-crossId")
        .generalConditions("General conditions")
        .tags(List.of("tag1", "tag2"))
        .excludedGroups(List.of("excludedGroup1", "excludedGroup2"))
        .validation(PlanValidation.AUTO)
        .selectionRule("{#request.attribute['selectionRule'] != null}")
        .security(BASE_PLAN_SECURITY.build())
        .flows(List.of(FlowFixtures.aFlowV2()));

    private static final UpdatePlanV4.UpdatePlanV4Builder BASE_UPDATE_PLAN_V4 = UpdatePlanV4
        .builder()
        .name("My plan")
        .description("Description")
        .order(1)
        .characteristics(List.of("characteristic1", "characteristic2"))
        .commentMessage("Comment message")
        .crossId("my-plan-crossId")
        .generalConditions("General conditions")
        .tags(List.of("tag1", "tag2"))
        .excludedGroups(List.of("excludedGroup1", "excludedGroup2"))
        .validation(PlanValidation.AUTO)
        .selectionRule("{#request.attribute['selectionRule'] != null}")
        .security(BASE_UPDATE_PLAN_SECURITY.build())
        .flows(List.of(FlowFixtures.aFlowV4()));

    private static final UpdatePlanV2.UpdatePlanV2Builder BASE_UPDATE_PLAN_V2 = UpdatePlanV2
        .builder()
        .name("My plan")
        .description("Description")
        .order(1)
        .characteristics(List.of("characteristic1", "characteristic2"))
        .commentMessage("Comment message")
        .crossId("my-plan-crossId")
        .generalConditions("General conditions")
        .tags(List.of("tag1", "tag2"))
        .excludedGroups(List.of("excludedGroup1", "excludedGroup2"))
        .validation(PlanValidation.AUTO)
        .selectionRule("{#request.attribute['selectionRule'] != null}")
        .security(BASE_UPDATE_PLAN_SECURITY.build())
        .flows(List.of(FlowFixtures.aFlowV2()));

    private static final io.gravitee.rest.api.model.PlanEntity.PlanEntityBuilder BASE_PLAN_ENTITY_V2 = io.gravitee.rest.api.model.PlanEntity
        .builder()
        .id("my-plan")
        .api("my-api")
        .name("My plan")
        .description("Description")
        .order(1)
        .characteristics(List.of("characteristic1", "characteristic2"))
        .createdAt(new Date())
        .updatedAt(new Date())
        .commentMessage("Comment message")
        .crossId("my-plan-crossId")
        .generalConditions("General conditions")
        .tags(Set.of("tag1", "tag2"))
        .status(io.gravitee.rest.api.model.PlanStatus.PUBLISHED)
        .security(io.gravitee.rest.api.model.PlanSecurityType.API_KEY)
        .type(io.gravitee.rest.api.model.PlanType.API)
        .excludedGroups(List.of("excludedGroup1", "excludedGroup2"))
        .validation(io.gravitee.rest.api.model.PlanValidationType.AUTO)
        .selectionRule("{#request.attribute['selectionRule'] != null}")
        .flows(List.of(FlowFixtures.aModelFlowV2()));

    public static PlanEntity aPlanEntityV4() {
        return BASE_PLAN_ENTITY_V4.build();
    }

    public static CreatePlanV4 aCreatePlanV4() {
        return BASE_CREATE_PLAN_V4.build();
    }

    public static CreatePlanV2 aCreatePlanV2() {
        return BASE_CREATE_PLAN_V2.build();
    }

    public static UpdatePlanV4 anUpdatePlanV4() {
        return BASE_UPDATE_PLAN_V4.build();
    }

    public static UpdatePlanV2 anUpdatePlanV2() {
        return BASE_UPDATE_PLAN_V2.build();
    }

    public static io.gravitee.rest.api.model.PlanEntity aPlanEntityV2() {
        return BASE_PLAN_ENTITY_V2.build();
    }

    public static PlanSecurity aPlanSecurityV4() {
        return BASE_PLAN_SECURITY_V4.build();
    }
}
