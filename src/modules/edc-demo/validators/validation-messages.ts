import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class ValidationMessages {
  invalidUrlMessage = 'Must be valid URL, e.g. https://example.com';
  invalidJsonMessage = 'Must be valid JSON';
  invalidWhitespacesMessage = 'Must not contain whitespaces.';
  invalidPrefix = (field: string, prefix: string): string =>
    `${field} must start with "${prefix}".`;
  invalidDateRangeMessage = 'Need valid date range.';
}
