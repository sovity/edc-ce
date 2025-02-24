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
