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
import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  BehaviorSubject,
  Observable,
  Subject,
  distinctUntilChanged,
  sampleTime,
  switchMap,
} from 'rxjs';
import {filter, map, takeUntil} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {ConnectorLimitsService} from '../../../../core/services/connector-limits.service';
import {DataOffer} from '../../../../core/services/models/data-offer';
import {value$} from '../../../../core/utils/form-group-utils';
import {AssetDetailDialogDataService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../shared/business/asset-detail-dialog/asset-detail-dialog.service';
import {CatalogBrowserFetchDetailDialogComponent} from '../catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.component';
import {CatalogBrowserFetchDetailDialogData} from '../catalog-browser-fetch-detail-dialog/catalog-browser-fetch-detail-dialog.data';
import {CatalogApiUrlService} from './catalog-api-url.service';
import {CatalogBrowserPageService} from './catalog-browser-page-service';
import {emptyCatalogBrowserPageData} from './catalog-browser-page.data';

@Component({
  selector: 'catalog-browser-page',
  templateUrl: './catalog-browser-page.component.html',
  styleUrls: ['./catalog-browser-page.component.scss'],
})
export class CatalogBrowserPageComponent implements OnInit, OnDestroy {
  data = emptyCatalogBrowserPageData();
  data$ = new BehaviorSubject(this.data);
  searchText = new FormControl('');
  customProviders = '';
  presetProvidersMessage = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private catalogBrowserPageService: CatalogBrowserPageService,
    private catalogApiUrlService: CatalogApiUrlService,
    private matDialog: MatDialog,
    private connectorLimitsService: ConnectorLimitsService,
    private translateService: TranslateService,
  ) {}

  ngOnInit(): void {
    this.catalogBrowserPageService
      .contractOfferPageData$(
        this.fetch$.pipe(sampleTime(200)),
        this.searchText$(),
      )
      .subscribe((data) => {
        this.data = data;
        this.data$.next(data);
      });
    this.startBuildingPresetCatalogUrlsMessage();
  }

  onDataOfferClick(dataOffer: DataOffer) {
    this.connectorLimitsService
      .isConsumingAgreementLimitExceeded()
      .pipe(
        switchMap((isConsumingLimitsExceeded) => {
          const data = this.assetDetailDialogDataService.dataOfferDetails(
            dataOffer,
            isConsumingLimitsExceeded,
          );
          return this.assetDetailDialogService.open(data, this.ngOnDestroy$);
        }),
        filter((it) => !!it?.refreshList),
      )
      .subscribe(() => this.fetch$.next(null));
  }

  onShowFetchDetails() {
    const data: CatalogBrowserFetchDetailDialogData = {
      data$: this.data$,
      refresh: () => this.fetch$.next(null),
    };
    this.matDialog.open(CatalogBrowserFetchDetailDialogComponent, {data});
  }

  onCatalogUrlsChange(): void {
    this.catalogApiUrlService.setCustomProvidersAsString(this.customProviders);
    this.fetch$.next(null);
  }

  private startBuildingPresetCatalogUrlsMessage() {
    this.translateService
      .get(['catalog_browser_page.usage'])
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((strings) => {
        const urls = this.catalogApiUrlService.getPresetProviders();
        const usage = strings['catalog_browser_page.usage'];
        this.presetProvidersMessage = !urls.length
          ? ''
          : `${usage} ${
              urls.length > 1 ? ` (${urls.length})` : ''
            }: ${urls.join(', ')}`;
      });
  }

  private searchText$(): Observable<string> {
    return (value$(this.searchText) as Observable<string>).pipe(
      map((it) => (it ?? '').trim()),
      distinctUntilChanged(),
    );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();

    // Reset selected Connector Endpoints
    this.catalogApiUrlService.setCustomProviders([]);
  }
}
