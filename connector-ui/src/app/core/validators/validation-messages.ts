/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class ValidationMessages {
  invalidEmailMessage = 'Must be a valid E-Mail address.';
  invalidUrlMessage = 'Must be valid URL, e.g. https://example.com';
  invalidJsonMessage = 'Must be valid JSON';
  invalidWhitespacesOrColonsMessage = 'Must not contain whitespaces or colons.';
  invalidPrefix = (field: string, prefix: string): string =>
    `${field} must start with "${prefix}".`;
  invalidDateRangeMessage = 'Need valid date range.';
  idExistsErrorMessage = 'ID already exists.';
  invalidQueryParam = "Must not contain '=' or '&' characters.";
}
