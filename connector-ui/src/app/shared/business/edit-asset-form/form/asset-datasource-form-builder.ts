/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {switchDisabledControls} from 'src/app/core/utils/form-group-utils';
import {jsonValidator} from 'src/app/core/validators/json-validator';
import {urlValidator} from 'src/app/core/validators/url-validator';
import {validQueryParam} from 'src/app/core/validators/valid-query-param';
import {assetDatasourceFormEnabledCtrls} from './model/asset-datasource-form-enabled-ctrls';
import {
  AssetDatasourceFormModel,
  AssetDatasourceFormValue,
} from './model/asset-datasource-form-model';
import {
  HttpDatasourceHeaderFormModel,
  HttpDatasourceHeaderFormValue,
} from './model/http-datasource-header-form-model';
import {
  HttpDatasourceQueryParamFormModel,
  HttpDatasourceQueryParamFormValue,
} from './model/http-datasource-query-param-form-model';

@Injectable()
export class AssetDatasourceFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(
    initial: AssetDatasourceFormValue,
  ): FormGroup<AssetDatasourceFormModel> {
    const datasource: FormGroup<AssetDatasourceFormModel> =
      this.formBuilder.nonNullable.group({
        dataSourceAvailability: initial?.dataSourceAvailability!,

        dataAddressType: initial?.dataAddressType!,
        dataDestination: [
          initial?.dataDestination!,
          [Validators.required, jsonValidator],
        ],

        // On-Request
        contactEmail: [
          initial?.contactEmail!,
          [Validators.required, Validators.email],
        ],
        contactPreferredEmailSubject: [
          initial?.contactPreferredEmailSubject!,
          Validators.required,
        ],

        // Http Datasource Fields
        httpUrl: [initial?.httpUrl!, [Validators.required, urlValidator]],
        httpMethod: [initial?.httpMethod!, Validators.required],

        httpAuthHeaderType: [initial?.httpAuthHeaderType!],
        httpAuthHeaderName: [initial?.httpAuthHeaderName!, Validators.required],
        httpAuthHeaderValue: [
          initial?.httpAuthHeaderValue!,
          Validators.required,
        ],
        httpAuthHeaderSecretName: [
          initial?.httpAuthHeaderSecretName!,
          Validators.required,
        ],
        httpQueryParams: this.formBuilder.array(
          initial?.httpQueryParams?.map(
            (param: HttpDatasourceQueryParamFormValue) =>
              this.buildQueryParamFormGroup(param),
          ) ?? [],
        ),

        httpDefaultPath: [initial?.httpDefaultPath!],
        httpProxyMethod: [initial?.httpProxyMethod!],
        httpProxyPath: [initial?.httpProxyPath!],
        httpProxyQueryParams: [initial?.httpProxyQueryParams!],
        httpProxyBody: [initial?.httpProxyBody!],

        httpHeaders: this.formBuilder.array(
          initial?.httpHeaders?.map((header: HttpDatasourceHeaderFormValue) =>
            this.buildHeaderFormGroup(header),
          ) ?? [],
        ),
      });

    switchDisabledControls<AssetDatasourceFormValue>(
      datasource,
      assetDatasourceFormEnabledCtrls,
    );

    return datasource;
  }

  buildHeaderFormGroup(
    initial: HttpDatasourceHeaderFormValue,
  ): FormGroup<HttpDatasourceHeaderFormModel> {
    return this.formBuilder.nonNullable.group({
      headerName: [initial.headerName!, Validators.required],
      headerValue: [initial.headerValue!, Validators.required],
    });
  }

  buildQueryParamFormGroup(
    initial: HttpDatasourceQueryParamFormValue,
  ): FormGroup<HttpDatasourceQueryParamFormModel> {
    return this.formBuilder.nonNullable.group({
      paramName: [initial.paramName!, [Validators.required, validQueryParam]],
      paramValue: [initial.paramValue!, [validQueryParam]],
    });
  }
}
