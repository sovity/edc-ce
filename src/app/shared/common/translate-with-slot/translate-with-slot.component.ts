import {Component, Input, OnChanges, OnDestroy} from '@angular/core';
import {Subject, switchMap} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {SimpleChangesTyped} from '../../../core/utils/angular-utils';

@Component({
  selector: 'translate-with-slot',
  templateUrl: './translate-with-slot.component.html',
})
export class TranslateWithSlotComponent implements OnChanges, OnDestroy {
  @Input()
  key!: string;

  @Input()
  html = false;

  key$ = new Subject();

  textBefore = '';
  hasMiddle = false;
  textAfter = '';

  constructor(private translationService: TranslateService) {
    this.key$
      .pipe(
        switchMap(() => this.translationService.get(this.key)),
        takeUntil(this.ngOnDestroy$),
      )
      .subscribe((text) => {
        const parts = text.split('{}');
        this.textBefore = parts[0];
        this.hasMiddle = parts.length > 1;
        this.textAfter = parts[1] || '';
      });
  }

  ngOnChanges(changes: SimpleChangesTyped<TranslateWithSlotComponent>) {
    if (changes.key) {
      this.key$.next(changes.key);
    }
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
