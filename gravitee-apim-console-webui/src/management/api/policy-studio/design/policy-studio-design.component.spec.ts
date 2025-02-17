/*
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpTestingController } from '@angular/common/http/testing';
import { InteractivityChecker } from '@angular/cdk/a11y';
import { Subject } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';

import { PolicyStudioDesignComponent } from './policy-studio-design.component';
import { PolicyStudioDesignModule } from './policy-studio-design.module';

import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../../shared/testing';
import { fakePolicyListItem } from '../../../../entities/policy';
import { fakeApi } from '../../../../entities/api/Api.fixture';
import { fakeResourceListItem } from '../../../../entities/resource/resourceListItem.fixture';
import { AjsRootScope, CurrentUserService, UIRouterStateParams } from '../../../../ajs-upgraded-providers';
import { User } from '../../../../entities/user';
import { fakeFlowSchema } from '../../../../entities/flow/flowSchema.fixture';
import { PolicyStudioService } from '../policy-studio.service';
import { ApiDefinition, toApiDefinition } from '../models/ApiDefinition';

describe('PolicyStudioDesignComponent', () => {
  let fixture: ComponentFixture<PolicyStudioDesignComponent>;
  let component: PolicyStudioDesignComponent;
  let httpTestingController: HttpTestingController;
  let policyStudioService: PolicyStudioService;
  let apiDefinition: ApiDefinition;

  const currentUser = new User();
  currentUser.userApiPermissions = ['api-plan-r', 'api-plan-u'];

  const apiFlowSchema = fakeFlowSchema();
  const policies = [fakePolicyListItem()];
  const api = fakeApi();
  const resources = [fakeResourceListItem()];

  const dialog = {
    open: jest.fn().mockReturnValue({
      afterClosed: jest.fn().mockReturnValue(new Subject()),
    }),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, GioHttpTestingModule, PolicyStudioDesignModule],
      providers: [
        { provide: UIRouterStateParams, useValue: { apiId: api.id } },
        {
          provide: CurrentUserService,
          useValue: { currentUser },
        },
        { provide: AjsRootScope, useValue: null },
        {
          provide: MatDialog,
          useValue: dialog,
        },
      ],
    })
      .overrideProvider(InteractivityChecker, {
        useValue: {
          isFocusable: () => true, // This traps focus checks and so avoid warnings when dealing with
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(PolicyStudioDesignComponent);
    component = fixture.componentInstance;

    httpTestingController = TestBed.inject(HttpTestingController);
    policyStudioService = TestBed.inject(PolicyStudioService);
    apiDefinition = toApiDefinition(api);
    policyStudioService.setApiDefinition(apiDefinition);
    fixture.detectChanges();

    httpTestingController.expectOne(`${CONSTANTS_TESTING.env.baseURL}/apis/schema`).flush(apiFlowSchema);

    httpTestingController.expectOne(`${CONSTANTS_TESTING.env.baseURL}/policies?expand=schema&expand=icon`).flush(policies);

    httpTestingController.expectOne(`${CONSTANTS_TESTING.env.baseURL}/resources?expand=schema&expand=icon`).flush(resources);
  });

  describe('ngOnInit', () => {
    it('should setup properties', async () => {
      expect(component.apiFlowSchema).toStrictEqual(apiFlowSchema);
      expect(component.policies).toStrictEqual(policies);
      expect(component.resourceTypes).toStrictEqual(resources);
      expect(component.apiDefinition).toStrictEqual(apiDefinition);
    });
  });

  it('should disable field when origin is kubernetes', async () => {
    const api = fakeApi({
      definition_context: { origin: 'kubernetes' },
    });
    policyStudioService.setApiDefinition(toApiDefinition(api));
    expect(component.isReadonly).toEqual(true);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should open dialog calling on display-policy-cta event', async () => {
    component.displayPolicyCta();

    expect(dialog.open).toHaveBeenCalledWith(expect.any(Function), {
      data: {
        featureInfo: {
          description:
            'This policy is part of Gravitee Enterprise. Enterprise policies allows you to easily define and customise rules according to your evolving business needs.',
          image: 'assets/gio-ee-unlock-dialog/policies.svg',
        },
        trialURL:
          'https://gravitee.io/self-hosted-trial?utm_source=oss_apim&utm_medium=v2_policy_studio_policy&utm_campaign=oss_apim_to_ee_apim',
      },
      id: 'gioLicenseDialog',
      role: 'alertdialog',
    });
  });
});
