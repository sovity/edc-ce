import {Component, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
import {switchMap, takeUntil} from 'rxjs/operators';
import {DashboardData, defaultDashboardData} from './dashboard-data';
import {DashboardDataService} from './dashboard-data.service';

@Component({
  selector: 'edc-demo-dashboard',
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit, OnDestroy {
  data: DashboardData = defaultDashboardData();

  private refresh$ = new BehaviorSubject(true);

  constructor(private dashboardDataService: DashboardDataService) {}

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
