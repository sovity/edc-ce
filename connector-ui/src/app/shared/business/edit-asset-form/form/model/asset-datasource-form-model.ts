/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  FormArray,
  FormControl,
  FormGroup,
  ɵFormGroupValue,
} from '@angular/forms';
import {UiDataSourceHttpDataMethod} from '@sovity.de/edc-client';
import {DataAddress} from './data-address';
import {DatasourceType} from './datasource-type';
import {HttpDatasourceAuthHeaderType} from './http-datasource-auth-header-type';
import {HttpDatasourceHeaderFormModel} from './http-datasource-header-form-model';
import {HttpDatasourceQueryParamFormModel} from './http-datasource-query-param-form-model';

/**
 * Form Model for Edit Asset Form > Datasource
 */
export interface AssetDatasourceFormModel {
  dataSourceAvailability: FormControl<DatasourceType>;

  dataAddressType: FormControl<DataAddress>;

  // Custom Datasource JSON
  dataDestination: FormControl<string>;

  // On-Request Datasource
  contactEmail: FormControl<string>;
  contactPreferredEmailSubject: FormControl<string>;

  // Http Datasource
  httpUrl: FormControl<string>;
  httpMethod: FormControl<UiDataSourceHttpDataMethod>;

  httpAuthHeaderType: FormControl<HttpDatasourceAuthHeaderType>;
  httpAuthHeaderName: FormControl<string>;
  httpAuthHeaderValue: FormControl<string>;
  httpAuthHeaderSecretName: FormControl<string>;
  httpHeaders: FormArray<FormGroup<HttpDatasourceHeaderFormModel>>;
  httpQueryParams: FormArray<FormGroup<HttpDatasourceQueryParamFormModel>>;
  httpProxyMethod: FormControl<boolean>;
  httpProxyPath: FormControl<boolean>;
  httpProxyQueryParams: FormControl<boolean>;
  httpProxyBody: FormControl<boolean>;
  httpDefaultPath: FormControl<string>;
}

/**
 * Form Value for Edit Asset Form > Datasource
 */
export type AssetDatasourceFormValue =
  ɵFormGroupValue<AssetDatasourceFormModel>;
