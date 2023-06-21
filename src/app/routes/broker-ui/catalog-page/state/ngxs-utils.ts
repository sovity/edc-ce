import {Injectable} from '@angular/core';
import {sampleTime} from 'rxjs';
import {Actions, Store, ofActionDispatched} from '@ngxs/store';

@Injectable({providedIn: 'root'})
export class NgxsUtils {
  constructor(private actions$: Actions, private store: Store) {}

  sampleTime(
    debounceActionType: any,
    executeActionType: any,
    sampleTimeMs: number,
  ) {
    this.actions$
      .pipe(ofActionDispatched(debounceActionType), sampleTime(sampleTimeMs))
      .subscribe(() => {
        this.store.dispatch(executeActionType);
      });
  }
}
