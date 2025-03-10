/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     Fraunhofer FIT - contributed initial internationalization support
 *     sovity - continued development
 */
import {AssetRequestBuilder} from 'src/app/core/services/asset-request-builder';
import {policyFormRequiredViewProviders} from '../policy-editor/editor/policy-form-required-providers';
import {AssetAdvancedFormBuilder} from './form/asset-advanced-form-builder';
import {AssetDatasourceFormBuilder} from './form/asset-datasource-form-builder';
import {AssetGeneralFormBuilder} from './form/asset-general-form-builder';
import {EditAssetForm} from './form/edit-asset-form';
import {EditAssetFormInitializer} from './form/edit-asset-form-initializer';

export const editAssetFormRequiredViewProviders = [
  EditAssetFormInitializer,
  AssetRequestBuilder,
  EditAssetForm,
  AssetGeneralFormBuilder,
  AssetDatasourceFormBuilder,
  AssetAdvancedFormBuilder,
  ...policyFormRequiredViewProviders,
];
