/*
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
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Injectable} from '@angular/core';
import {UiAssetCreateRequest} from '@sovity.de/edc-client';
import {AssetCreateDialogFormValue} from '../../routes/connector-ui/asset-list-page/asset-create-dialog/form/model/asset-create-dialog-form-model';
import {toGmtZeroHourDate} from '../utils/date-utils';
import {AssetDataSourceMapperLegacy} from './asset-data-source-mapper-legacy';
import {AssetRequestCommonMetadata} from './asset-request-common-metadata';

@Injectable()
export class AssetRequestBuilderLegacy {
  constructor(private assetDataSourceMapper: AssetDataSourceMapperLegacy) {}

  buildAssetCreateRequestLegacy(
    formValue: AssetCreateDialogFormValue,
  ): UiAssetCreateRequest {
    const id = formValue.metadata?.id!;
    const metadata = this.buildAssetRequestCommonMetadataLegacy(formValue);
    const dataSource = this.assetDataSourceMapper.buildDataSourceLegacy(
      formValue.datasource!,
    );
    return {
      id,
      ...metadata,
      dataSource,
    };
  }

  buildAssetRequestCommonMetadataLegacy(
    formValue: AssetCreateDialogFormValue,
  ): AssetRequestCommonMetadata {
    const title = formValue.metadata?.title!;
    const version = formValue.metadata?.version;
    const description = formValue.metadata?.description;
    const language = formValue.metadata?.language?.id;
    const keywords = formValue.metadata?.keywords;
    const licenseUrl = formValue.metadata?.standardLicense;
    const publisherHomepage = formValue.metadata?.publisher;
    const mediaType = formValue.metadata?.contentType;
    const landingPageUrl = formValue.metadata?.endpointDocumentation;

    const dataCategory = formValue.advanced?.dataCategory?.id;
    const dataSubcategory = formValue.advanced?.dataSubcategory?.id;
    const transportMode = formValue.advanced?.transportMode?.id;
    const geoReferenceMethod = formValue.advanced?.geoReferenceMethod;
    const dataModel = formValue.advanced?.dataModel;

    const sovereignLegalName = formValue.advanced?.sovereignLegalName;
    const geoLocation = formValue.advanced?.geoLocation;
    const nutsLocations = formValue.advanced?.nutsLocations;
    const dataSampleUrls = formValue.advanced?.dataSampleUrls;
    const referenceFileUrls = formValue.advanced?.referenceFileUrls;
    const referenceFilesDescription =
      formValue.advanced?.referenceFilesDescription;
    const conditionsForUse = formValue.advanced?.conditionsForUse;
    const dataUpdateFrequency = formValue.advanced?.dataUpdateFrequency;
    let temporalCoverageFrom =
      formValue.advanced?.temporalCoverage?.from || undefined;
    let temporalCoverageToInclusive =
      formValue.advanced?.temporalCoverage?.toInclusive || undefined;
    temporalCoverageFrom = temporalCoverageFrom
      ? toGmtZeroHourDate(temporalCoverageFrom)
      : undefined;
    temporalCoverageToInclusive = temporalCoverageToInclusive
      ? toGmtZeroHourDate(temporalCoverageToInclusive)
      : undefined;

    return {
      title,
      language,
      description,
      publisherHomepage,
      licenseUrl,
      version,
      keywords,
      mediaType,
      landingPageUrl,
      dataCategory,
      dataSubcategory,
      dataModel,
      geoReferenceMethod,
      transportMode,
      sovereignLegalName,
      geoLocation,
      nutsLocations,
      dataSampleUrls,
      referenceFileUrls,
      referenceFilesDescription,
      conditionsForUse,
      dataUpdateFrequency,
      temporalCoverageFrom,
      temporalCoverageToInclusive,
      customJsonAsString: undefined,
      customJsonLdAsString: undefined,
      privateCustomJsonAsString: undefined,
      privateCustomJsonLdAsString: undefined,
    };
  }
}
