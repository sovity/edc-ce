/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
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
      if (value.publishMode !== 'DO_NOT_PUBLISH') {
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
