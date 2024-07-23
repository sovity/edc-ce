import {Component, OnDestroy} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {UiPolicyExpression} from '@sovity.de/edc-client';
import {validDateRange} from '../../../../../core/validators/valid-date-range-optional-end';
import {ValidationMessages} from '../../../../../core/validators/validation-messages';
import {buildTimespanRestriction} from './timespan-restriction-expression';

@Component({
  selector: 'timespan-restriction-dialog',
  templateUrl: './timespan-restriction-dialog.component.html',
})
export class TimespanRestrictionDialogComponent implements OnDestroy {
  group = this.formBuilder.nonNullable.group({
    range: this.formBuilder.group(
      {
        start: null as Date | null,
        end: null as Date | null,
      },
      {validators: validDateRange},
    ),
  });

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<TimespanRestrictionDialogComponent>,
    public validationMessages: ValidationMessages,
  ) {}

  onAdd() {
    const formValue = this.group.value;

    const expression = buildTimespanRestriction(
      formValue.range!.start!,
      formValue.range!.end!,
    );

    this.close(expression);
  }

  private close(params: UiPolicyExpression) {
    this.dialogRef.close(params);
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy(): void {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
