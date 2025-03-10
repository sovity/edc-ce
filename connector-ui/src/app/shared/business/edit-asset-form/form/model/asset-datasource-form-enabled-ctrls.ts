/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  AssetDatasourceFormModel,
  AssetDatasourceFormValue,
} from './asset-datasource-form-model';

export const assetDatasourceFormEnabledCtrls = (
  value: AssetDatasourceFormValue,
): Record<keyof AssetDatasourceFormModel, boolean> => {
  const customDataAddressJson =
    value.dataAddressType === 'Custom-Data-Address-Json';

  const onRequest = value.dataSourceAvailability === 'On-Request';
  const datasource = value.dataSourceAvailability === 'Datasource';

  const http = value.dataAddressType === 'Http' && datasource;
  const httpAuth = value.httpAuthHeaderType !== 'None';
  const httpAuthByValue = value.httpAuthHeaderType === 'Value';
  const httpAuthByVault = value.httpAuthHeaderType === 'Vault-Secret';
  const proxyPath = !!value.httpProxyPath;

  return {
    dataSourceAvailability: true,

    // On Request Datasource
    contactEmail: onRequest,
    contactPreferredEmailSubject: onRequest,

    dataAddressType: datasource,

    // Custom Datasource JSON
    dataDestination: datasource && customDataAddressJson,

    // Http Datasource Fields
    httpUrl: http,
    httpMethod: http && !value.httpProxyMethod,

    httpAuthHeaderType: http,
    httpAuthHeaderName: http && httpAuth,
    httpAuthHeaderValue: http && httpAuthByValue,
    httpAuthHeaderSecretName: http && httpAuthByVault,
    httpQueryParams: http,

    httpDefaultPath: http && proxyPath,
    httpProxyMethod: http,
    httpProxyPath: http,
    httpProxyQueryParams: http,
    httpProxyBody: http,

    httpHeaders: http,
  };
};
