/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type InitiateTransferFormValue} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-schema';
import {type InitiateTransferHttpFormValue} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-http-form';
import {
  getAuthFields,
  getBaseUrlWithoutQueryParams,
  getFullQueryString,
} from '@/app/(authenticated)/data-offers/create/components/ui-data-source-mapper';
import {mapKeys, removeNullValues} from '@/lib/utils/map-utils';

export const buildDataAddressProperties = (
  formValue: InitiateTransferFormValue,
): Record<string, string> => {
  switch (formValue.transferType) {
    case 'CUSTOM_JSON':
      // eslint-disable-next-line @typescript-eslint/no-unsafe-return
      return JSON.parse(formValue.dataAddressJson.trim());
    case 'HTTP':
      return buildHttpDataAddress(formValue);
    default:
      throw new Error(`Invalid Data Address Type`);
  }
};

const EDC = 'https://w3id.org/edc/v0.0.1/ns/';

export const DataAddressProperty = {
  authCode: `${EDC}authCode`,
  authKey: `${EDC}authKey`,
  baseUrl: `${EDC}baseUrl`,
  body: `${EDC}body`,
  header: `header`,
  contentType: `${EDC}contentType`,
  mediaType: `${EDC}mediaType`,
  method: `${EDC}method`,
  pathSegments: `${EDC}pathSegments`,
  proxyBody: `${EDC}proxyBody`,
  proxyMethod: `${EDC}proxyMethod`,
  proxyPath: `${EDC}proxyPath`,
  proxyQueryParams: `${EDC}proxyQueryParams`,
  queryParams: `${EDC}queryParams`,
  secretName: `${EDC}secretName`,
  type: `${EDC}type`,
};

const buildHttpDataAddress = (
  formValue: InitiateTransferHttpFormValue,
): Record<string, string> => {
  const {authHeaderName, authHeaderValue, authHeaderSecretName} =
    getAuthFields(formValue);

  const method = formValue.httpMethod?.trim().toUpperCase() || null;

  const baseUrl = getBaseUrlWithoutQueryParams(formValue.httpUrl);
  const queryParams = getFullQueryString(formValue.httpUrl, []);

  const headers = buildHttpHeaders(formValue.httpAdditionalHeaders ?? []);
  const props: Record<string, string | null> = {
    [DataAddressProperty.type]: 'HttpData',
    [DataAddressProperty.baseUrl]: baseUrl,
    [DataAddressProperty.method]: method,
    [DataAddressProperty.authKey]: authHeaderName,
    [DataAddressProperty.authCode]: authHeaderValue,
    [DataAddressProperty.secretName]: authHeaderSecretName,
    [DataAddressProperty.queryParams]: queryParams,
    ...mapKeys(headers, (k) => {
      if (k.toLowerCase() === 'content-type') {
        // this is required because the EDC sends the Content-Type header if and only if provided using the special field "contentType"
        return DataAddressProperty.contentType;
      }
      return `${DataAddressProperty.header}:${k}`;
    }),
  };
  return removeNullValues(props);
};

const buildHttpHeaders = (
  headers: {key: string; value: string}[],
): Record<string, string> => {
  // eslint-disable-next-line @typescript-eslint/no-unsafe-return
  return Object.fromEntries(
    headers
      .map((header) => [header.key.trim(), header.value.trim()])
      .filter((a) => a[0] && a[1]),
  );
};
