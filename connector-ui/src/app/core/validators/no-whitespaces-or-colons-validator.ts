/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {ValidatorFn, Validators} from '@angular/forms';

/**
 * Validates whether value contains whitespaces
 * @param control control
 */
export const noWhitespacesOrColonsValidator: ValidatorFn =
  Validators.pattern(/^[^\s:]*$/);
