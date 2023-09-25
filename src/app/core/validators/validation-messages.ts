import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class ValidationMessages {
  invalidUrlMessage = 'Must be valid URL, e.g. https://example.com';
  invalidJsonMessage = 'Must be valid JSON';
  invalidWhitespacesOrColonsMessage = 'Must not contain whitespaces or colons.';
  invalidPrefix = (field: string, prefix: string): string =>
    `${field} must start with "${prefix}".`;
  invalidDateRangeMessage = 'Need valid date range.';
  idExistsErrorMessage = 'ID already exists.';
}
