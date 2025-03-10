/*
 * Copyright 2022 Eclipse Foundation and Contributors
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
 *     Eclipse Foundation - initial setup of the eclipse-edc/DataDashboard UI
 *     sovity - continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Observable, distinctUntilChanged} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {Fetched} from '../../../../core/services/models/fetched';
import {value$} from '../../../../core/utils/form-group-utils';
import {ContractDefinitionEditorDialogResult} from '../contract-definition-editor-dialog/contract-definition-editor-dialog-result';
import {ContractDefinitionEditorDialog} from '../contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {ContractDefinitionPageData} from './contract-definition-page.data';
import {ContractDefinitionPageService} from './contract-definition-page.service';

@Component({
  selector: 'app-contract-definition-page',
  templateUrl: './contract-definition-page.component.html',
  styleUrls: ['./contract-definition-page.component.scss'],
})
export class ContractDefinitionPageComponent implements OnInit {
  contractDefinitionList: Fetched<ContractDefinitionPageData> = Fetched.empty();
  searchText = new FormControl<string>('');
  deleteBusy = false;

  private fetch$ = new BehaviorSubject(null);

  constructor(
    private contractDefinitionPageService: ContractDefinitionPageService,
    private readonly dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.contractDefinitionPageService
      .contractDefinitionPageData$(this.fetch$, this.searchText$())
      .subscribe((contractDefinitionList) => {
        this.contractDefinitionList = contractDefinitionList;
      });
  }

  onCreate() {
    const dialogRef = this.dialog.open(ContractDefinitionEditorDialog);
    dialogRef
      .afterClosed()
      .pipe(
        map((it) => it as ContractDefinitionEditorDialogResult | null),
        filter((it) => !!it?.refreshList),
      )
      .subscribe(() => this.refresh());
  }

  refresh() {
    this.fetch$.next(null);
  }

  private searchText$(): Observable<string> {
    return (value$(this.searchText) as Observable<string>).pipe(
      map((it) => (it ?? '').trim()),
      distinctUntilChanged(),
    );
  }
}
