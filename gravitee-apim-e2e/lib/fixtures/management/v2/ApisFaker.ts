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
import faker from '@faker-js/faker';
import { CreateApiV4, DefinitionVersion } from '../../../management-v2-webclient-sdk/src/lib/models';

export class ApisV4Faker {
  static version() {
    const major = faker.datatype.number({ min: 1, max: 5 });
    const minor = faker.datatype.number({ min: 1, max: 10 });
    const patch = faker.datatype.number({ min: 1, max: 30 });
    return `${major}.${minor}.${patch}`;
  }

  static newApi(attributes?: Partial<CreateApiV4>): CreateApiV4 {
    const name = faker.commerce.productName();
    const apiVersion = this.version();
    const description = faker.commerce.productDescription();

    const newApiEntityV4: CreateApiV4 = {
      apiVersion,
      type: 'MESSAGE',
      definitionVersion: DefinitionVersion.V4,
      description,
      name,
      listeners: [
        {
          type: 'HTTP',
          paths: [{ path: 'jest/apiV4' }],
          entrypoints: [
            {
              type: 'http-get',
              configuration: {
                messagesLimitCount: 500,
                messagesLimitDurationMs: 5000,
                headersInPayload: false,
                metadataInPayload: false,
              },
              qos: 'auto',
            },
          ],
        },
      ],
      endpointGroups: [
        {
          name: 'default-group',
          type: 'kafka',
          endpoints: [
            {
              name: 'default',
              type: 'kafka',
              weight: 1,
              inheritConfiguration: false,
              configuration: { bootstrapServers: 'kafka:9092' },
              sharedConfigurationOverride: { consumer: { enabled: true, topics: ['demo'], autoOffsetReset: 'earliest' } },
            },
          ],
        },
      ],
      ...attributes,
    };

    return newApiEntityV4;
  }
}
