/*
 * Copyright 2022 Eclipse Foundation and Contributors
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
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
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {UiAsset} from '@sovity.de/edc-client';
import {DataCategorySelectItem} from '../../../shared/form-elements/data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../../shared/form-elements/data-subcategory-select/data-subcategory-select-item';
import {LanguageSelectItem} from '../../../shared/form-elements/language-select/language-select-item';
import {TransportModeSelectItem} from '../../../shared/form-elements/transport-mode-select/transport-mode-select-item';

/**
 * UiAsset with replaced fixed vocabulary items.
 *
 * This exists, because certain metadata has labels which are added in the UI, e.g. language.
 */
export type UiAssetMapped = Omit<
  UiAsset,
  'language' | 'dataCategory' | 'dataSubcategory' | 'transportMode'
> & {
  connectorEndpoint: string;

  language: LanguageSelectItem | null;

  // MDS Specific
  dataCategory: DataCategorySelectItem | null;
  dataSubcategory: DataSubcategorySelectItem | null;
  transportMode: TransportModeSelectItem | null;

  // Unhandled Additional Properties
  customJsonProperties: AdditionalAssetProperty[];
  customJsonLdProperties: AdditionalAssetProperty[];
  privateCustomJsonProperties: AdditionalAssetProperty[];
  privateCustomJsonLdProperties: AdditionalAssetProperty[];
};

export interface AdditionalAssetProperty {
  key: string;
  value: string;
}
