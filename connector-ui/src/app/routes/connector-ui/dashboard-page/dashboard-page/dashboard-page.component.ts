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
import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {switchMap, takeUntil} from 'rxjs/operators';
import {APP_CONFIG, AppConfig} from 'src/app/core/config/app-config';
import {ConnectorInfoPropertyGridGroupBuilder} from '../../../../core/services/connector-info-property-grid-group-builder';
import {DashboardPageData, defaultDashboardData} from './dashboard-page-data';
import {DashboardPageDataService} from './dashboard-page-data.service';

@Component({
  selector: 'dashboard-page',
  templateUrl: './dashboard-page.component.html',
  providers: [ConnectorInfoPropertyGridGroupBuilder],
})
export class DashboardPageComponent implements OnInit, OnDestroy {
  data: DashboardPageData = defaultDashboardData();
  private refresh$ = new BehaviorSubject(true);

  constructor(
    @Inject(APP_CONFIG) public config: AppConfig,
    private dashboardDataService: DashboardPageDataService,
  ) {}

  ngOnInit() {
    this.refresh$
      .pipe(
        switchMap(() => this.dashboardDataService.getDashboardData()),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe((data) => {
        this.data = data;
      });
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
