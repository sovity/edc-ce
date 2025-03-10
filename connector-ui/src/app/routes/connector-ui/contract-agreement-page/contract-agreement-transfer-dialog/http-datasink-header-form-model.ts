/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {FormControl, ɵFormGroupValue} from '@angular/forms';

/**
 * Form Model for ContractAgreementTransferDialog > Datasink > HTTP/REST > Header
 */
export interface HttpDatasinkHeaderFormModel {
  headerName: FormControl<string>;
  headerValue: FormControl<string>;
}

/**
 * Form Value for ContractAgreementTransferDialog > Datasink > HTTP/REST > Header
 */
export type HttpDatasinkHeaderFormValue =
  ɵFormGroupValue<HttpDatasinkHeaderFormModel>;
