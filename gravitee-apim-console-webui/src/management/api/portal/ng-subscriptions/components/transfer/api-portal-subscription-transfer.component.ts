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
import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { GioConnectorDialogData } from '../../../../../../components/gio-connector-dialog/gio-connector-dialog.component';
import { Plan } from '../../../../../../entities/management-api-v2';
import {ApiPlanV2Service} from "../../../../../../services-ngx/api-plan-v2.service";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";

export interface SubscriptionTransferData {
  apiId: string;
  planId: string;
  subscriptionId: string;
}
@Component({
  selector: 'api-portal-subscription-transfer',
  template: require('./api-portal-subscription-transfer.component.html'),
  styles: [require('./api-portal-subscription-transfer.component.scss')],
})
export class ApiPortalSubscriptionTransferComponent {
  private unsubscribe$: Subject<boolean> = new Subject<boolean>();
  plans: Plan[];
  selectedPlan: Plan;
  showGeneralConditionsMsg: boolean;
  form: FormGroup;
  constructor(private readonly dialogRef: MatDialogRef<Plan>,
              @Inject(MAT_DIALOG_DATA) dialogData: SubscriptionTransferData,
              private readonly apiPlanService: ApiPlanV2Service) {
    this.apiPlanService.list(dialogData.apiId)
      .pipe(
        takeUntil(this.unsubscribe$)
      ).subscribe(plans => {
        this.form = new FormGroup(
          {planFormControl: new FormControl()}
        )
        this.plans = plans.data.filter(plan => plan.id !== dialogData.planId);
        this.showGeneralConditionsMsg = this.plans.some(plan => (plan.generalConditions))
    })
  }

  onClose() {
    console.log(this.form.getRawValue())
    this.dialogRef.close(this.selectedPlan);
  }

  transferSubscription() {
    this.onClose();
  }
}
