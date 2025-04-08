/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type DataOfferLiveFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-form';
import {type DataOfferOnRequestFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-on-request-form';
import {type DataOfferTypeFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {type DataOfferCreateFormModel} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {type UiAsset} from '@sovity.de/edc-client';

export const dataOfferFormValueForEdit = (
  asset: UiAsset,
): DataOfferCreateFormModel => {
  const empty = dataOfferFormValueForCreate();
  return {
    ...empty,
    type: {
      ...empty.type,
      offerType: 'UNCHANGED',
    },
    general: {
      ...empty.general,
      assetId: asset.assetId,
      title: asset.title,
      description: asset.description,
      keywords: asset.keywords,
    },
    advanced: {
      ...empty.advanced,
      showAdvancedFields: true,

      language: asset.language ?? undefined,
      version: asset.version ?? undefined,

      publisher: asset.publisherHomepage ?? undefined,
      standardLicense: asset.licenseUrl ?? undefined,
      contentType: asset.mediaType ?? undefined,
      endpointDocumentation: asset.landingPageUrl ?? undefined,
      dataModel: asset.dataModel ?? undefined,
      geoReferenceMethod: asset.geoReferenceMethod ?? undefined,
      sovereignLegalName: asset.sovereignLegalName ?? undefined,
      geoLocation: asset.geoLocation ?? undefined,
      dataSampleUrls: asset.dataSampleUrls?.map((url) => ({value: url})) ?? [],
      referenceFileUrls:
        asset.referenceFileUrls?.map((url) => ({value: url})) ?? [],
      referenceFilesDescription: asset.referenceFilesDescription ?? undefined,
      conditionsForUse: asset.conditionsForUse ?? undefined,
      dataUpdateFrequency: asset.dataUpdateFrequency ?? undefined,
      temporalCoverage: {
        startDate: asset.temporalCoverageFrom
          ? new Date(asset.temporalCoverageFrom)
          : undefined,
        endDate: asset.temporalCoverageToInclusive
          ? new Date(asset.temporalCoverageToInclusive)
          : undefined,
      },
    },
  };
};

export const dataOfferFormValueForCreate = (): DataOfferCreateFormModel => {
  const defaultDataSourceHttp: DataOfferLiveFormValue = {
    offerType: 'LIVE',
    live: {
      offerLiveType: 'HTTP',
      httpMethod: 'GET',
      httpUrl: '',
      httpQueryParams: [],
      httpAdditionalHeaders: [],
      auth: {type: 'NONE'},
      httpMethodParameterization: false,
      httpPathParameterization: false,
      httpQueryParamsParameterization: false,
      httpRequestBodyParameterization: false,
    },
  };

  const defaultDataSourceOnRequest: DataOfferOnRequestFormValue = {
    offerType: 'ON_REQUEST',
    contactEmail: '',
    contactPreferredEmailSubject: '',
  };

  return {
    type: {
      ...defaultDataSourceHttp,
      ...defaultDataSourceOnRequest,
    } as DataOfferTypeFormValue,
    general: {
      assetId: '',
      title: '',
      description: '',
      keywords: [],
    },
    advanced: {
      showAdvancedFields: false,
    },
    publishing: {
      mode: 'PUBLISH_UNRESTRICTED',
      policy: {},
    } as DataOfferCreateFormModel['publishing'],
  };
};
