import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {switchMap, takeUntil} from 'rxjs/operators';
import {PropertyGridField} from '../../../../component-library/property-grid/property-grid/property-grid-field';
import {APP_CONFIG, AppConfig} from '../../../../core/config/app-config';
import {ConnectorInfoPropertyGridBuilder} from '../../../../core/services/connector-info-property-grid-builder';
import {DashboardPageData, defaultDashboardData} from './dashboard-page-data';
import {DashboardPageDataService} from './dashboard-page-data.service';

@Component({
  selector: 'dashboard-page',
  templateUrl: './dashboard-page.component.html',
  providers: [ConnectorInfoPropertyGridBuilder],
})
export class DashboardPageComponent implements OnInit, OnDestroy {
  data: DashboardPageData = defaultDashboardData();
  connectorProperties: PropertyGridField[] = [];

  private refresh$ = new BehaviorSubject(true);

  constructor(
    @Inject(APP_CONFIG) public config: AppConfig,
    private dashboardDataService: DashboardPageDataService,
    private connectorInfoPropertyGridBuilder: ConnectorInfoPropertyGridBuilder,
  ) {}

  ngOnInit() {
    this.connectorProperties =
      this.connectorInfoPropertyGridBuilder.buildPropertyGrid();
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
