import {HttpErrorResponse} from '@angular/common/http';
import {Injectable, OnDestroy} from '@angular/core';
import {EMPTY, Observable, Subject, interval} from 'rxjs';
import {catchError, switchMap, takeUntil} from 'rxjs/operators';
import {EdcApiService} from './api/edc-api.service';

@Injectable({providedIn: 'root'})
export class LoginPollingService implements OnDestroy {
  private pollInterval = 1000 * 30;
  private ngOnDestroy$ = new Subject();

  constructor(private edcApiService: EdcApiService) {}

  startPolling(): void {
    interval(this.pollInterval)
      .pipe(
        switchMap(() => this.pollLogin()),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe();
  }

  private pollLogin(): Observable<unknown> {
    return this.edcApiService.getAssetPage().pipe(
      catchError((err) => {
        if (!(err instanceof HttpErrorResponse) || err.status !== 401) {
          console.warn('Error while polling for session', err);
        }
        return EMPTY;
      }),
    );
  }

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
