import {Injectable} from '@angular/core';
import {validUrlPattern} from '../../validators/url-validator';
import {PropertyGridField} from './property-grid-field';

@Injectable({providedIn: 'root'})
export class PropertyGridUtils {
  guessValue(
    value: string | null | undefined,
  ): Pick<PropertyGridField, 'url' | 'text' | 'additionalClasses'> {
    return {
      text: value || '-',
      url: value?.match(validUrlPattern) ? value : undefined,
      additionalClasses: value?.includes(' ') ? undefined : 'break-all',
    };
  }
}
