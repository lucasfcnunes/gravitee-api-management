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

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { GioIconsModule } from '@gravitee/ui-particles-angular';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FormlyModule } from '@ngx-formly/core';

import { ResourceTypeComponent } from './resource-type.component';
import { ResourceTypeService } from './resource-type.service';

import { GioSafePipeModule } from '../../utils/gio.pipe.module';

@NgModule({
  declarations: [ResourceTypeComponent],
  exports: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,

    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,

    FormlyModule.forChild({
      types: [
        {
          name: 'resource-type',
          component: ResourceTypeComponent,
        },
      ],
    }),

    GioIconsModule,
    GioSafePipeModule,
  ],
  providers: [ResourceTypeService],
})
export class SpecificJsonSchemaTypeModule {}
