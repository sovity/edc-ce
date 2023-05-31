import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {
  BehaviorSubject,
  Observable,
  Subject,
  distinctUntilChanged,
  sampleTime,
} from 'rxjs';
import {map} from 'rxjs/operators';
import {value$} from '../../../../core/utils/form-group-utils';
import {emptyConnectorPageStateModel} from './connector-page-data';
import {ConnectorPageDataService} from './connector-page-data.service';

@Component({
  selector: 'connector-page',
  templateUrl: './connector-page.component.html',
  styleUrls: ['./connector-page.component.scss'],
})
export class ConnectorPageComponent implements OnInit, OnDestroy {
  data = emptyConnectorPageStateModel();
  data$ = new BehaviorSubject(this.data);
  searchText = new FormControl('');
  private fetch$ = new BehaviorSubject(null);

  constructor(private catalogBrowserPageService: ConnectorPageDataService) {}

  ngOnInit(): void {
    this.catalogBrowserPageService
      .connectorPageData$(this.fetch$.pipe(sampleTime(200)), this.searchText$())
      .subscribe((data) => {
        this.data = data;
        this.data$.next(data);
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
  }
}
