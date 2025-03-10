/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {removeUndefinedValues} from '../utils/record-utils';

@Injectable({providedIn: 'root'})
export class MailtoLinkBuilder {
  private readonly MAILTO = 'mailto:';

  buildMailtoUrl(
    email: string,
    subject?: string,
    body?: string,
    cc?: string,
    bcc?: string,
  ): string {
    const queryParams = new URLSearchParams(
      removeUndefinedValues({
        subject,
        body,
        cc,
        bcc,
      }),
    );
    // URLSearchParams replaces spaces with '+', so we need to replace them with '%20' for the mail scenario
    const queryParamsStr = queryParams.toString().replaceAll('+', '%20');
    const queryStr = queryParamsStr ? `?${queryParamsStr}` : '';

    return `${this.MAILTO}${email}${queryStr}`;
  }
}
