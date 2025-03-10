/*
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 * Copyright 2025 sovity GmbH
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
 *     Fraunhofer FIT - contributed initial internationalization support
 *     sovity - continued development
 */
import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {NEVER, Observable} from 'rxjs';
import {showDialogUntil} from '../../../../core/utils/mat-dialog-utils';
import {AssetCreateDialogData} from './asset-create-dialog-data';
import {AssetCreateDialogResult} from './asset-create-dialog-result';
import {AssetCreateDialogComponent} from './asset-create-dialog.component';
import {AssetCreateDialogFormMapper} from './form/asset-create-dialog-form-mapper';

@Injectable()
export class AssetCreateDialogService {
  constructor(
    private dialog: MatDialog,
    private assetCreateDialogFormMapper: AssetCreateDialogFormMapper,
  ) {}

  showCreateDialog(
    until$: Observable<any> = NEVER,
  ): Observable<AssetCreateDialogResult | undefined> {
    const initialFormValue = this.assetCreateDialogFormMapper.forCreate();
    return this._open({initialFormValue}, until$);
  }

  private _open(
    data: AssetCreateDialogData,
    until$: Observable<any>,
  ): Observable<AssetCreateDialogResult | undefined> {
    return showDialogUntil(
      this.dialog,
      AssetCreateDialogComponent,
      {data},
      until$,
    );
  }
}
