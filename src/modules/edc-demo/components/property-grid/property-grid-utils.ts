import {formatDate} from '@angular/common';
import {Inject, Injectable, LOCALE_ID} from '@angular/core';
import {validUrlPattern} from '../../validators/url-validator';
import {PropertyGridField} from './property-grid-field';

@Injectable({providedIn: 'root'})
export class PropertyGridUtils {
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

  formatDate(date: Date | null | undefined): string {
    if (!date) {
      return '';
    }

    return formatDate(date, 'yyyy-MM-dd HH:mm:ss', this.locale);
  }
}
