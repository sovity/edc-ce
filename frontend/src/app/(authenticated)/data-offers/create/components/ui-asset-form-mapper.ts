/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type DataOfferCreateFormModel} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {buildUiDataSource} from '@/app/(authenticated)/data-offers/create/components/ui-data-source-mapper';
import {toGmtZeroHourDate} from '@/lib/utils/date-utils';
import {
  type UiAssetCreateRequest,
  type UiAssetEditRequest,
} from '@sovity.de/edc-client';

export const buildUiAssetCreateRequest = (
  formValue: DataOfferCreateFormModel,
): UiAssetCreateRequest => {
  return {
    dataSource: buildUiDataSource(formValue.type),
    id: formValue.general.assetId,
    ...buildUiAssetCommonMetadata(formValue),
  };
};

export const buildUiAssetEditRequest = (
  formValue: DataOfferCreateFormModel,
): UiAssetEditRequest => {
  return {
    dataSourceOverrideOrNull:
      formValue.type.offerType === 'UNCHANGED'
        ? undefined
        : buildUiDataSource(formValue.type),
    ...buildUiAssetCommonMetadata(formValue),
  };
};

export const buildUiAssetCommonMetadata = (
  formValue: DataOfferCreateFormModel,
): UiAssetCommonMetadata => {
  const showAdvancedFields = formValue.advanced.showAdvancedFields;
  return {
    title: formValue.general.title || undefined,
    description: formValue.general.description || undefined,
    keywords: formValue.general.keywords,
    language: showAdvancedFields ? formValue.advanced.language : undefined,
    version: showAdvancedFields
      ? formValue.advanced.version || undefined
      : undefined,

    publisherHomepage: showAdvancedFields
      ? formValue.advanced.publisher || undefined
      : undefined,
    licenseUrl: showAdvancedFields
      ? formValue.advanced.standardLicense || undefined
      : undefined,
    mediaType: showAdvancedFields
      ? formValue.advanced.contentType || undefined
      : undefined,
    landingPageUrl: showAdvancedFields
      ? formValue.advanced.endpointDocumentation || undefined
      : undefined,
    dataModel: showAdvancedFields
      ? formValue.advanced.dataModel || undefined
      : undefined,
    geoReferenceMethod: showAdvancedFields
      ? formValue.advanced.geoReferenceMethod || undefined
      : undefined,
    sovereignLegalName: showAdvancedFields
      ? formValue.advanced.sovereignLegalName || undefined
      : undefined,
    geoLocation: showAdvancedFields
      ? formValue.advanced.geoLocation || undefined
      : undefined,
    dataSampleUrls: showAdvancedFields
      ? formValue.advanced.dataSampleUrls?.map((sample) => sample.value).length
        ? formValue.advanced.dataSampleUrls.map((sample) => sample.value)
        : undefined
      : undefined,
    referenceFileUrls: showAdvancedFields
      ? formValue.advanced.referenceFileUrls?.map(
          (reference) => reference.value,
        ).length
        ? formValue.advanced.referenceFileUrls.map(
            (reference) => reference.value,
          )
        : undefined
      : undefined,
    referenceFilesDescription: showAdvancedFields
      ? formValue.advanced.referenceFilesDescription || undefined
      : undefined,
    conditionsForUse: showAdvancedFields
      ? formValue.advanced.conditionsForUse || undefined
      : undefined,
    dataUpdateFrequency: showAdvancedFields
      ? formValue.advanced.dataUpdateFrequency || undefined
      : undefined,
    temporalCoverageFrom:
      showAdvancedFields && formValue.advanced.temporalCoverage?.startDate
        ? toGmtZeroHourDate(formValue.advanced.temporalCoverage.startDate)
        : undefined,
    temporalCoverageToInclusive:
      showAdvancedFields && formValue.advanced.temporalCoverage?.endDate
        ? toGmtZeroHourDate(formValue.advanced.temporalCoverage.endDate)
        : undefined,
  };
};

export type UiAssetCommonMetadata = Omit<
  UiAssetCreateRequest,
  'id' | 'dataSource'
> &
  Omit<UiAssetEditRequest, 'dataSourceOverrideOrNull'>;
