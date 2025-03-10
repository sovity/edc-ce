/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for AssetCreateDialog > Datasource > HTTP/REST > Header
 */
export interface HttpDatasourceQueryParamFormModel {
  paramName: FormControl<string>;
  paramValue: FormControl<string>;
}

/**
 * Form Value for AssetCreateDialog > Datasource > HTTP/REST > QueryParam
 */
export type HttpDatasourceQueryParamFormValue =
  ɵFormGroupValue<HttpDatasourceQueryParamFormModel>;
