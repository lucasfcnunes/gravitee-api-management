#!/usr/bin/env zx
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
import {$, fs} from 'zx';

await $`mkdir -p .tmp`;
await $`sed s/'"uniqueItems":true'/'"uniqueItems":false'/g ../gravitee-apim-rest-api/gravitee-apim-rest-api-management-v2/gravitee-apim-rest-api-management-v2-rest/src/main/resources/management-openapi-v2.yaml > .tmp/management-openapi-v2.yaml`;

// magic sed to properly handle ListenerV4's inheritance
await $`
export var1='"allOf":\\[{"$ref":"#/components/schemas/ListenerV4"},{"type":"object","properties":{'
export var2='"allOf":\\[{"type":"object","properties":{"type":{"type":"string"},"entrypoints":{"type":"array","items":{"$ref":"#/components/schemas/EntrypointV4"}},'
sed -i.bak s@$var1@$var2@g .tmp/management-openapi-v2.yaml`;
await $`
export var1='"allOf":\\[{"$ref":"#/components/schemas/ListenerV4"}\\]'
export var2='"allOf":\\[{"type":"object","properties":{"type":{"type":"string"},"entrypoints":{"type":"array","items":{"$ref":"#/components/schemas/EntrypointV4"}}}}\\]'
sed -i.bak s@$var1@$var2@g .tmp/management-openapi-v2.yaml`;

// magic sed to properly handle multipart/form-data endpoints
await $`
export var1='{"multipart/form-data":{'
export var2='{"*/*":{'
sed -i.bak s@$var1@$var2@g .tmp/management-openapi-v2.yaml`;

await $`npx @openapitools/openapi-generator-cli@2.5.2 generate \\
          -i .tmp/management-openapi-v2.yaml \\
          -g typescript-fetch \\
          -o lib/management-v2-webclient-sdk/src/lib/ \\
          -puseSingleRequestParameter=true \\
          -pmodelPropertyNaming=original \\
          -penumPropertyNaming=UPPERCASE \\
          -psortModelPropertiesByRequiredFlag=false \\
          -psortParamsByRequiredFlag=false \\
          --import-mappings=DateTime=Date \\
          --type-mappings=DateTime=Date,object=any \\
          --reserved-words-mappings=configuration=configuration`

await $`find lib/management-v2-webclient-sdk/src/lib/ -name "*.ts" -exec sed -i.bak "/* The version of the OpenAPI document/d" {} \+`;
// Magic `sed` to properly handle `text/plain` in TS SDK, until we have a proper way to handle it in the OpenAPI generator
await $`
  export var1="overridedInit.body instanceof URLSearchParams ||"
  export var2="overridedInit.body instanceof URLSearchParams || typeof overridedInit.body === 'string' ||"
  find lib/management-v2-webclient-sdk/src/lib/ -name "*.ts" -exec sed -i.bak "s/$var1/$var2/g" {} \+`;

// must delete .bak files
await $`find lib/management-v2-webclient-sdk/src/lib/ -name "*.ts.bak" -exec rm -f {} \+`;
await $`rm -f lib/management-v2-webclient-sdk/src/lib/index.ts`;
await $`rm -f lib/management-v2-webclient-sdk/src/lib/apis/index.ts`;
await $`rm .tmp/management-openapi-v2*`;

//Regenerate index.ts for models
await $`rm lib/management-v2-webclient-sdk/src/lib/models/index.ts`;
const models = await $`ls lib/management-v2-webclient-sdk/src/lib/models/`;
const modelsModuleName = models.stdout.split('\n').map((model) => model.replace('.ts', ''));
fs.writeFileSync('lib/management-v2-webclient-sdk/src/lib/models/index.ts', modelsModuleName.map((model) => `export * from './${model}';`).join('\n'));
