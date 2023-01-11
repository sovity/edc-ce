import {Inject, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class ValidationMessages {
  invalidUrlMessage = "Must be valid URL, e.g. https://example.com"
  invalidJsonMessage = "Must be valid JSON"
  invalidWhitespacesMessage = "Must not contain whitespaces."
  invalidIdPrefix = "ID must start with \"urn:artifact:\"."
  invalidDateRangeMessage = "Need valid date range.";
}
