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
import { HarnessLoader, parallel } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatIconTestingModule } from '@angular/material/icon/testing';
import { HighchartsChartModule } from 'highcharts-angular';

import { HomeApiHealthCheckComponent } from './home-api-health-check.component';

import { CurrentUserService, UIRouterState, UIRouterStateParams } from '../../../ajs-upgraded-providers';
import { User } from '../../../entities/user';
import { CONSTANTS_TESTING, GioHttpTestingModule } from '../../../shared/testing';
import { HomeModule } from '../home.module';
import { Api } from '../../../entities/api';
import { fakePagedResult } from '../../../entities/pagedResult';
import { fakeApi } from '../../../entities/api/Api.fixture';
import { GioUiRouterTestingModule } from '../../../shared/testing/gio-uirouter-testing-module';
import { GioQuickTimeRangeHarness } from '../widgets/gio-quick-time-range/gio-quick-time-range.harness';

describe('HomeApiHealthCheckComponent', () => {
  let fixture: ComponentFixture<HomeApiHealthCheckComponent>;
  let loader: HarnessLoader;
  let httpTestingController: HttpTestingController;

  const currentUser = new User();
  const fakeUiRouter = { go: jest.fn() };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        GioUiRouterTestingModule,
        GioHttpTestingModule,
        HomeModule,
        MatIconTestingModule,
        HighchartsChartModule,
      ],
      providers: [
        { provide: UIRouterState, useValue: fakeUiRouter },
        { provide: CurrentUserService, useValue: { currentUser } },
        { provide: UIRouterStateParams, useValue: {} },
      ],
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeApiHealthCheckComponent);

    httpTestingController = TestBed.inject(HttpTestingController);
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  afterEach(() => {
    httpTestingController.verify();
    jest.clearAllMocks();
  });

  it('should display an empty table', async () => {
    fixture.detectChanges();

    await expectApisListRequest([]);

    const { headerCells, rowCells } = await computeApisTableCells();
    expect(headerCells).toEqual([
      {
        actions: '',
        name: 'Name',
        picture: '',
        states: '',
        availability: 'API Availability',
      },
    ]);
    expect(rowCells).toEqual([['No APIs to display.']]);
  });

  it('should display a table with one row', async () => {
    fixture.detectChanges();
    const api = fakeApi({
      healthcheck_enabled: false,
    });

    await expectApisListRequest([api]);

    const { headerCells, rowCells } = await computeApisTableCells();
    expect(headerCells).toEqual([
      {
        actions: '',
        name: 'Name',
        picture: '',
        states: '',
        availability: 'API Availability',
      },
    ]);
    expect(rowCells).toEqual([['', '🪐 Planets (1.0)', '', 'Health-check has not been configured', '']]);
  });

  it('should display api with HeathCheck configured', async () => {
    fixture.detectChanges();
    const api = fakeApi({
      healthcheck_enabled: true,
    });

    await expectApisListRequest([api]);
    fixture.detectChanges();
    expectGetApiHealth(api.id);
    expectGetApiHealthAverage(api.id);
    fixture.detectChanges();

    const { headerCells, rowCells } = await computeApisTableCells();
    expect(headerCells).toEqual([
      {
        actions: '',
        name: 'Name',
        picture: '',
        states: '',
        availability: 'API Availability',
      },
    ]);
    expect(rowCells).toEqual([
      ['', '🪐 Planets (1.0)', '', '50%Created with Highcharts 9.2.213:21:3013:21:4013:21:5013:22:0013:22:1013:22:20', ''],
    ]);

    // Expect HealthCheck TimeFrame select changes
    const healthCheckTimeFrameSelect = await loader.getHarness(GioQuickTimeRangeHarness);
    await healthCheckTimeFrameSelect.selectTimeRangeByText('last week');
    expectGetApiHealthAverage(api.id);

    const { rowCells: rowCells_2 } = await computeApisTableCells();
    expect(rowCells_2).toEqual([
      ['', '🪐 Planets (1.0)', '', '20%Created with Highcharts 9.2.213:21:3013:21:4013:21:5013:22:0013:22:1013:22:20', ''],
    ]);
  });

  describe('onViewHealthCheckClicked', () => {
    beforeEach(async () => {
      fixture.detectChanges();

      await expectApisListRequest([fakeApi()]);
    });

    it('should navigate to view API HealthCheck', async () => {
      await loader
        .getHarness(MatButtonHarness.with({ selector: '[aria-label="Button to view API Health-check"]' }))
        .then((button) => button.click());

      expect(fakeUiRouter.go).toHaveBeenCalledWith('management.apis.detail.proxy.healthCheckDashboard.visualize', { apiId: fakeApi().id });
    });
  });

  describe('onRefreshClick', () => {
    const api = fakeApi({
      healthcheck_enabled: true,
    });
    beforeEach(async () => {
      fixture.detectChanges();

      await expectApisListRequest([api]);
      fixture.detectChanges();
      expectGetApiHealth(api.id);
      expectGetApiHealthAverage(api.id);
      fixture.detectChanges();
    });

    it('should fetch last Health data', async () => {
      await loader.getHarness(MatButtonHarness.with({ selector: '.time-frame__refresh-btn' })).then((button) => button.click());

      expectGetApiHealth(api.id);
      expectGetApiHealthAverage(api.id);
    });
  });

  describe('onOnlyHCConfigured', () => {
    const api = fakeApi({
      healthcheck_enabled: true,
    });
    beforeEach(async () => {
      fixture.detectChanges();

      await expectApisListRequest([api]);
      fixture.detectChanges();
      expectGetApiHealth(api.id);
      expectGetApiHealthAverage(api.id);
      fixture.detectChanges();
    });

    it("should filter by 'has_health_check:true'", async () => {
      await loader.getHarness(MatButtonHarness.with({ text: 'Filter by Heath-Check enabled only' })).then((button) => button.click());

      expectApisListRequest([api], 'has_health_check:true');
    });
  });
  async function computeApisTableCells() {
    const table = await loader.getHarness(MatTableHarness.with({ selector: '#apisTable' }));

    const headerRows = await table.getHeaderRows();
    const headerCells = await parallel(() => headerRows.map((row) => row.getCellTextByColumnName()));

    const rows = await table.getRows();
    const rowCells = await parallel(() => rows.map((row) => row.getCellTextByIndex()));
    return { headerCells, rowCells };
  }

  async function expectApisListRequest(apis: Api[] = [], q?: string, order?: string, page = 1) {
    await fixture.whenStable();

    const req = httpTestingController.expectOne(
      `${CONSTANTS_TESTING.env.baseURL}/apis/_search/_paged?page=${page}&size=10&q=${q ? q : '*'}${order ? `&order=${order}` : ''}`,
    );
    expect(req.request.method).toEqual('POST');
    req.flush(fakePagedResult(apis));
  }

  function expectGetApiHealth(apiId: string) {
    const req = httpTestingController.expectOne({
      url: `${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}/health?type=availability`,
      method: 'GET',
    });
    req.flush({
      global: {
        '1d': 10,
        '1w': 20,
        '1h': 30,
        '1M': 40,
        '1m': 50,
      },
      buckets: {
        default: {
          '1d': 10,
          '1w': 20,
          '1h': 30,
          '1M': 40,
          '1m': 50,
        },
      },
      metadata: {
        default: {
          target: 'https://apim-master-gateway.team-apim.gravitee.dev',
        },
      },
    });
  }

  function expectGetApiHealthAverage(apiId: string) {
    const req = httpTestingController.expectOne((req) => {
      return req.method === 'GET' && req.url.startsWith(`${CONSTANTS_TESTING.env.baseURL}/apis/${apiId}/health/average`);
    });
    req.flush({
      timestamp: {
        from: 1679923284000,
        to: 1679923346000,
        interval: 2000,
      },
      values: [
        {
          buckets: [
            {
              name: 'by_available',
              data: [
                100.0, 0, 0, 100.0, 0, 100.0, 0, 0, 100.0, 0, 100.0, 0, 0, 100.0, 0, 100.0, 0, 0, 100.0, 0, 100.0, 0, 0, 100.0, 0, 100.0, 0,
                0, 100.0, 0, 0, 0,
              ],
            },
          ],
          field: 'available',
          name: 'by_available',
        },
      ],
    });
  }
});
