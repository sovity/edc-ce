/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {formatDate} from '@angular/common';
import {Inject, Injectable, LOCALE_ID} from '@angular/core';
import {validUrlPattern} from '../../../core/validators/url-validator';
import {PropertyGridField} from './property-grid-field';

@Injectable({providedIn: 'root'})
export class PropertyGridFieldService {
  constructor(@Inject(LOCALE_ID) private locale: string) {}

  guessValue(
    value: string | null | undefined,
  ): Pick<PropertyGridField, 'url' | 'text' | 'additionalClasses'> {
    return {
      text: value || '-',
      url: value?.match(validUrlPattern) ? value : undefined,
      additionalClasses: value?.includes(' ') ? undefined : 'break-all',
    };
  }

  formatDateWithTime(date: Date | null | undefined): string {
    if (!date) {
      return '';
    }

    return formatDate(date, 'dd/MM/yyyy HH:mm:ss', this.locale);
  }

  formatDate(date: Date | null | undefined): string {
    if (!date) {
      return '';
    }

    return formatDate(date, 'dd/MM/yyyy', this.locale);
  }
}
