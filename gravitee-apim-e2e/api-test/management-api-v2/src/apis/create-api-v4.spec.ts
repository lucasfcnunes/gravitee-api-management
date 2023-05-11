import { forManagementV2 } from '@gravitee/utils/configuration';
import { describe, expect, test } from '@jest/globals';
import { APIsApi, CreateApiRequest } from '../../../../lib/management-v2-webclient-sdk/src/lib/apis/APIsApi';
import { CreateApiV4 } from '../../../../lib/management-v2-webclient-sdk/src/lib/models';
import { ApisV4Faker } from '@gravitee/fixtures/management/v2/ApisFaker';

const envId = 'DEFAULT';
const configuration = forManagementV2();
const apisApi = new APIsApi(configuration);

describe('Create API', () => {
  test('should create an API v4', async () => {
    const createApiV4: CreateApiV4 = ApisV4Faker.newApi({ name: 'automated test' });
    const newApi: CreateApiRequest = {
      envId,
      createApiV4,
    };
    await apisApi.createApi(newApi).then((res) => {
      expect(res.name).toEqual('automated test');
    });
  });
});
