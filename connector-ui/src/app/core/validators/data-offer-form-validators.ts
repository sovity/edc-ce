import {Injectable} from '@angular/core';
import {AbstractControl, ValidationErrors, ValidatorFn} from '@angular/forms';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {IdAvailabilityResponse} from '@sovity.de/edc-client';
import {EdcApiService} from '../services/api/edc-api.service';

@Injectable({
  providedIn: 'root',
})
export class DataOfferFormValidators {
  constructor(private edcApiService: EdcApiService) {}

  policyIdExistsValidator: ValidatorFn = (
    control: AbstractControl,
  ): Observable<ValidationErrors | null> => {
    const value = control.value;
    if (value) {
      return this.edcApiService.isPolicyIdAvailable(value).pipe(
        catchError(() =>
          of<IdAvailabilityResponse>({id: value, available: false}),
        ),
        map((it) => (it.available ? null : {idAlreadyExists: true})),
      );
    }
    return of(null);
  };

  contractDefinitionIdExistsValidator: ValidatorFn = (
    control: AbstractControl,
  ): Observable<ValidationErrors | null> => {
    const value = control.value;
    if (value) {
      return this.edcApiService.isContractDefinitionIdAvailable(value).pipe(
        catchError(() =>
          of<IdAvailabilityResponse>({id: value, available: false}),
        ),
        map((it) => (it.available ? null : {idAlreadyExists: true})),
      );
    }
    return of(null);
  };

  assetIdExistsValidator: ValidatorFn = (
    control: AbstractControl,
  ): Observable<ValidationErrors | null> => {
    const value = control.value;
    if (value) {
      return this.edcApiService.isAssetIdAvailable(value).pipe(
        catchError(() =>
          of<IdAvailabilityResponse>({id: value, available: false}),
        ),
        map((it) => (it.available ? null : {idAlreadyExists: true})),
      );
    }
    return of(null);
  };
}
