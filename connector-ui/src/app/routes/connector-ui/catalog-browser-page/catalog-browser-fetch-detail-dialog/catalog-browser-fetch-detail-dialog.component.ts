/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {HttpErrorResponse} from '@angular/common/http';
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {DataOffer} from '../../../../core/services/models/data-offer';
import {Fetched} from '../../../../core/services/models/fetched';
import {CatalogBrowserPageData} from '../catalog-browser-page/catalog-browser-page.data';
import {CatalogBrowserFetchDetailDialogData} from './catalog-browser-fetch-detail-dialog.data';

@Component({
  selector: 'app-catalog-browser-fetch-detail-dialog',
  templateUrl: './catalog-browser-fetch-detail-dialog.component.html',
})
export class CatalogBrowserFetchDetailDialogComponent
  implements OnInit, OnDestroy
{
  data: CatalogBrowserPageData | null = null;

  constructor(
    public dialogRef: MatDialogRef<CatalogBrowserFetchDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public dialogData: CatalogBrowserFetchDetailDialogData,
  ) {}

  ngOnInit() {
    this.dialogData.data$
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((data) => {
        this.data = data;
      });
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  errorMessages(data: Fetched<DataOffer[]>): string[] {
    if (!data.isError) {
      return [];
    }

    const error = data.errorOrUndefined?.error;
    if (error instanceof HttpErrorResponse) {
      if (!error.status) {
        return [
          'Could not reach EDC backend. Please check your internet connection.',
        ];
      } else if (error.status === 502) {
        return [
          `Status ${error.status}`,
          `EDC Backend failed fetching other connector catalog.`,
          `Backend message: ${this.httpErrorResponseMessage(error)}`,
        ];
      } else {
        return [
          `Status ${error.status}`,
          `EDC Backend Error`,
          `Backend message: ${this.httpErrorResponseMessage(error)}`,
        ];
      }
    } else {
      return [error?.message ?? 'Unknown UI error.'];
    }
  }

  private httpErrorResponseMessage(error: HttpErrorResponse) {
    const childError = error.error;
    if (childError?.message) {
      return childError.message;
    } else if (childError) {
      return JSON.stringify(childError);
    } else if (error.message) {
      return error.message;
    } else {
      return error.statusText;
    }
  }
}
