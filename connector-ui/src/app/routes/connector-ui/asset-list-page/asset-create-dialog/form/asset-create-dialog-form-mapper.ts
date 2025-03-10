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
import {Injectable} from '@angular/core';
import {LanguageSelectItemService} from '../../../../../shared/form-elements/language-select/language-select-item.service';
import {AssetCreateDialogFormValue} from './model/asset-create-dialog-form-model';
import {AssetDatasourceFormValue} from './model/asset-datasource-form-model';

/**
 * Handles AngularForms for AssetCreateDialog
 */
@Injectable()
export class AssetCreateDialogFormMapper {
  constructor(private languageSelectItemService: LanguageSelectItemService) {}

  forCreate(): AssetCreateDialogFormValue {
    return {
      metadata: {
        id: '',
        title: '',
        version: '',
        contentType: '',
        description: '',
        keywords: [],
        language: this.languageSelectItemService.english(),
        publisher: '',
        standardLicense: '',
        endpointDocumentation: '',
      },
      advanced: {
        dataModel: '',
        dataCategory: null,
        dataSubcategory: null,
        transportMode: null,
        geoReferenceMethod: '',
      },
      datasource: this.emptyHttpDatasource(),
    };
  }

  private emptyHttpDatasource(): AssetDatasourceFormValue {
    return {
      dataAddressType: 'Http',
      dataDestination: '',

      httpUrl: '',
      httpMethod: 'GET',
      httpAuthHeaderType: 'None',
      httpAuthHeaderName: '',
      httpAuthHeaderValue: '',
      httpAuthHeaderSecretName: '',
      httpQueryParams: [],

      httpDefaultPath: '',
      httpProxyMethod: false,
      httpProxyPath: false,
      httpProxyQueryParams: false,
      httpProxyBody: false,

      httpHeaders: [],
    };
  }
}
