import {Injectable} from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
} from '@angular/forms';
import {Observable, combineLatest, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {IdAvailabilityResponse} from '@sovity.de/edc-client';
import {EdcApiService} from 'src/app/core/services/api/edc-api.service';
import {ALWAYS_TRUE_POLICY_ID} from './model/always-true-policy-id';
import {EditAssetFormValue} from './model/edit-asset-form-model';

/**
 * Handles AngularForms for Edit Asset Form
 */
@Injectable({providedIn: 'root'})
export class EditAssetFormValidators {
  constructor(private edcApiService: EdcApiService) {}

  /**
   * Use on asset control, reset asset control on publish mode changes, accesses parent form
   */
  isValidId(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const value = control?.parent?.parent?.value as EditAssetFormValue | null;
      if (value?.mode !== 'CREATE') {
        return of(null);
      }

      const assetId = control.value! as string;
      if (value.publishMode === 'PUBLISH_UNRESTRICTED') {
        return combineLatest([
          this.assetIdExistsErrorMessage(assetId),
          this.contractDefinitionIdErrorMessage(assetId),
          this.policyIdExistsErrorMessage(ALWAYS_TRUE_POLICY_ID).pipe(
            map((errorMessage) =>
              // We want to throw an error if always-true was not found
              errorMessage ? null : 'Always True Policy does not exist.',
            ),
          ),
        ]).pipe(
          map((errorMessages) => this.buildValidationErrors(errorMessages)),
        );
      } else if (value.publishMode === 'PUBLISH_RESTRICTED') {
        return combineLatest([
          this.assetIdExistsErrorMessage(assetId),
          this.contractDefinitionIdErrorMessage(assetId),
          this.policyIdExistsErrorMessage(assetId),
        ]).pipe(
          map((errorMessages) => this.buildValidationErrors(errorMessages)),
        );
      } else {
        return this.assetIdExistsErrorMessage(assetId).pipe(
          map((result) => this.buildValidationErrors([result])),
        );
      }

      return of(null);
    };
  }

  private assetIdExistsErrorMessage(id: string): Observable<string | null> {
    return this.edcApiService.isAssetIdAvailable(id).pipe(
      catchError(() => of<IdAvailabilityResponse>({id, available: false})),
      map((it) => (it.available ? null : 'Asset already exists.')),
    );
  }

  private contractDefinitionIdErrorMessage(
    id: string,
  ): Observable<string | null> {
    return this.edcApiService.isContractDefinitionIdAvailable(id).pipe(
      catchError(() => of<IdAvailabilityResponse>({id, available: false})),
      map((it) =>
        it.available ? null : 'Contract Definition already exists.',
      ),
    );
  }

  private policyIdExistsErrorMessage(id: string): Observable<string | null> {
    return this.edcApiService.isPolicyIdAvailable(id).pipe(
      catchError(() => of<IdAvailabilityResponse>({id, available: false})),
      map((it) => (it.available ? null : 'Policy already exists.')),
    );
  }

  private buildValidationErrors(
    errorMessages: (string | null)[],
  ): ValidationErrors | null {
    const errors = errorMessages.filter((it) => it);
    if (!errors.length) {
      return null;
    }

    const message =
      errors.length === 3 ? 'Data Offer already exists.' : errors.join(' ');

    return {exists: message};
  }
}
