/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type DataOfferLiveCustomFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-custom-form';
import {type DataOfferLiveFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-form';
import {type DataOfferLiveHttpFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-http-form';
import {type DataOfferOnRequestFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-on-request-form';
import {type DataOfferTypeFormValue} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {
  everythingAfter,
  everythingBefore,
  trimOrEmpty,
} from '@/lib/utils/string-utils';
import {
  type UiDataSource,
  type UiHttpAuth,
  UiHttpAuthType,
  UiHttpOauth2AuthType,
} from '@sovity.de/edc-client';
import {type DataOfferLiveAzureStorageFormValue} from './data-offer-live-azure-storage-form';

export const buildUiDataSource = (
  formValue: DataOfferTypeFormValue,
): UiDataSource => {
  if (formValue.offerType === 'ON_REQUEST') {
    return buildUiDataSourceOnRequest(formValue);
  } else if (formValue.offerType === 'LIVE') {
    return buildUiDataSourceLive(formValue);
  } else {
    throw new Error('Unknown data source type');
  }
};

export const buildUiDataSourceOnRequest = (
  formValue: DataOfferOnRequestFormValue,
): UiDataSource => {
  return {
    type: 'ON_REQUEST',
    onRequest: {
      contactEmail: formValue.contactEmail,
      contactPreferredEmailSubject: formValue.contactPreferredEmailSubject,
    },
  };
};

export const buildUiDataSourceLive = (
  formValue: DataOfferLiveFormValue,
): UiDataSource => {
  if (formValue.live.offerLiveType === 'CUSTOM_JSON') {
    return buildCustomDataSource(formValue.live);
  } else if (formValue.live.offerLiveType === 'HTTP') {
    return buildHttpDataSource(formValue.live);
  } else if (formValue.live.offerLiveType === 'AZURE_STORAGE') {
    return buildAzureStorageDataSource(formValue.live);
  } else {
    throw new Error('Unknown data source type');
  }
};

const buildCustomDataSource = (
  formValue: DataOfferLiveCustomFormValue,
): UiDataSource => {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
  const json = JSON.parse(formValue.dataAddressJson.trim()) as Record<
    string,
    string
  >;
  return {
    type: 'CUSTOM',
    customProperties: json,
  };
};

const buildHttpDataSource = (
  formValue: DataOfferLiveHttpFormValue,
): UiDataSource => {
  const baseUrl = getBaseUrlWithoutQueryParams(formValue.httpUrl);
  const queryString = getFullQueryString(
    formValue.httpUrl,
    formValue.httpQueryParams ?? [],
  );

  const auth = getAuth(formValue);

  return {
    type: 'HTTP_DATA',
    httpData: {
      method: formValue.httpMethod,
      baseUrl,
      queryString: queryString ?? undefined,
      auth: auth ?? undefined,
      headers: buildHttpHeaders(formValue.httpAdditionalHeaders ?? []),
      enableMethodParameterization: formValue.httpMethodParameterization,
      enablePathParameterization: formValue.httpPathParameterization,
      enableQueryParameterization: formValue.httpQueryParamsParameterization,
      enableBodyParameterization: formValue.httpRequestBodyParameterization,
    },
  };
};

const buildAzureStorageDataSource = (
  formValue: DataOfferLiveAzureStorageFormValue,
): UiDataSource => {
  return {
    type: 'AZURE_STORAGE',
    azureStorage: {
      blobName: formValue.blobName,
      containerName: formValue.containerName,
      storageAccountName: formValue.storageAccountName,
      accountKey: formValue.accountKey,
    },
  };
};

const getAuth = (formValue: DataOfferLiveHttpFormValue): UiHttpAuth | null => {
  const auth = formValue.auth;
  const authType = auth.type;
  if (authType === 'NONE') {
    return null;
  }

  if (authType === 'VAULT_SECRET') {
    return {
      type: UiHttpAuthType.ApiKey,
      apiKey: {
        headerName: auth.headerName,
        vaultKey: auth.headerSecretName,
      },
    };
  }

  if (authType === 'BASIC') {
    return {
      type: UiHttpAuthType.Basic,
      basic: {
        username: auth.username,
        password: auth.password,
      },
    };
  }

  if (authType === 'OAUTH2_CLIENT_CREDENTIALS') {
    return {
      type: UiHttpAuthType.Oauth2,
      oauth: {
        tokenUrl: auth.tokenUrl,
        scope: auth.scope,
        type: UiHttpOauth2AuthType.SharedSecret,
        sharedSecret: {
          clientId: auth.clientId,
          clientSecretName: auth.clientSecretKeyName,
        },
      },
    };
  }

  if (auth.type === 'OAUTH2_PRIVATE_KEY') {
    return {
      type: UiHttpAuthType.Oauth2,
      oauth: {
        tokenUrl: auth.tokenUrl,
        scope: auth.scope,
        type: UiHttpOauth2AuthType.PrivateKey,
        privateKey: {
          privateKeyName: auth.privateKeyName,
          tokenValidityInSeconds: +auth.tokenValidityInSeconds,
        },
      },
    };
  }

  throw new Error(`Unknown auth type: ${authType}`);
};

export const buildHttpHeaders = (
  headers: {key: string; value: string}[],
): Record<string, string> => {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  return Object.fromEntries(
    headers
      .map((header) => [header.key?.trim() || '', header.value?.trim() || ''])
      .filter((a) => a[0] && a[1]),
  );
};

export function getBaseUrlWithoutQueryParams(rawUrl: string): string {
  return everythingBefore('?', trimOrEmpty(rawUrl));
}

/**
 * Merges query params from the base URL with the additional ones.
 */
export function getFullQueryString(
  baseUrlWithQueryParams: string,
  additionalQueryParams: {key: string; value?: string}[],
): string | null {
  const queryParamSegments = additionalQueryParams.map((param) =>
    encodeQueryParam(param),
  );

  return [
    everythingAfter('?', trimOrEmpty(baseUrlWithQueryParams)),
    ...queryParamSegments,
  ]
    .filter((it) => !!it)
    .join('&');
}

export function encodeQueryParam(param: {key: string; value?: string}): string {
  const k = encodeURIComponent(trimOrEmpty(param.key));
  const v = encodeURIComponent(trimOrEmpty(param.value));
  return buildQueryParam(k, v);
}

export function buildQueryParam(name: string, value: string) {
  return `${name}=${value}`;
}
