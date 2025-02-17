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
import { IComponentControllerService } from 'angular';

import { setupAngularJsTesting } from '../../../../../jest.setup.js';

setupAngularJsTesting();

describe('LogComponent', () => {
  let $componentController: IComponentControllerService;
  let logComponent: any;

  beforeEach(inject((_QualityRuleService_, _$componentController_) => {
    $componentController = _$componentController_;
    const bindings = {
      log: {
        clientRequest: {
          uri: '/echo?param=1',
        },
        proxyRequest: {
          uri: '/echo?param=1',
        },
      },
    };
    logComponent = $componentController('log', null, bindings);
  }));

  describe('getMimeType', () => {
    it('returns the first data in Content Type header', () => {
      const mimeType = logComponent.getMimeType({ headers: { 'Content-Type': ['application/json', 'text/html'] } });
      expect(mimeType).toEqual('javascript');
    });

    it('returns null if there is no Content Type header', () => {
      const mimeType = logComponent.getMimeType({ headers: {} });
      expect(mimeType).toEqual(null);
    });
  });

  describe('extractQueryParams', () => {
    it('return different query params', () => {
      const queryParams = logComponent.extractQueryParams(`/gme?type=monthly&bucket=status_repartition&bucket=status_repartition-2`);
      expect(queryParams).toEqual([
        { key: 'type', value: 'monthly' },
        { key: 'bucket', value: '[ status_repartition, status_repartition-2 ]' },
      ]);
    });

    it('return query params with `=` in it', () => {
      const queryParams = logComponent.extractQueryParams(`/gme?type=monthly=status$test&param2=is=containing=some=equals&param3=value3`);
      expect(queryParams).toEqual([
        { key: 'type', value: 'monthly=status$test' },
        { key: 'param2', value: 'is=containing=some=equals' },
        { key: 'param3', value: 'value3' },
      ]);
    });
  });
});
