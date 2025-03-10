/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  AbstractControl,
  FormArray,
  FormControl,
  FormControlStatus,
  FormGroup,
} from '@angular/forms';
import {EMPTY, Observable, concat, distinctUntilChanged} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';

/**
 * Enables/Disables form groups controls
 */
export function switchDisabledControls<T>(
  ctrl: FormGroup,
  enabledCtrlsFn: (value: T) => Record<keyof T, boolean>,
) {
  const checkForChanges = () => {
    const enabledCtrls = enabledCtrlsFn(ctrl.value);
    const enabled = new Set<keyof T>(
      Object.entries(enabledCtrls)
        .filter(([_, v]) => v)
        .map(([k, _]) => k as keyof T),
    );
    Object.entries(ctrl.controls).forEach(([ctrlName, ctrl]) => {
      const ctrlNameTyped = ctrlName as keyof T;

      const currentlyDisabled = ctrl.disabled;
      const expectedDisabled = !enabled.has(ctrlNameTyped);
      if (currentlyDisabled == expectedDisabled) {
        return;
      }

      if (expectedDisabled) {
        ctrl.disable();
      } else {
        ctrl.enable();
      }
    });
  };

  status$(ctrl)
    .pipe(
      map((status) => status != 'DISABLED'),
      distinctUntilChanged(),
      switchMap((enabled) => (enabled ? value$(ctrl) : EMPTY)),
    )
    .subscribe(() => checkForChanges());
}

/**
 * Enables/Disables form controls depending on selected option.
 *
 * Use this when a select switches entire parts of the form.
 *
 * Disabling the controls will disable validation.
 */
export function switchDisabledControlsByField<
  T extends {[K in keyof T]: AbstractControl},
  S extends string | number | symbol,
>(opts: {
  /**
   * Form Group
   */
  formGroup: FormGroup<T>;

  /**
   * Select / Switch Control
   */
  switchCtrl: FormControl<S>;

  /**
   * Sets of fields to activate, keyed by switch value
   */
  enabledControlsByValue: {[K in S]: (keyof T)[]};
}) {
  const map = new Map(
    Object.entries(opts.enabledControlsByValue) as [S, (keyof T)[]][],
  );
  switchDisabledControlsByField2<T, S>({...opts, enabledControlsByValue: map});
}

/**
 * Enables/Disables form controls depending on selected option.
 *
 * Use this when a select switches entire parts of the form.
 *
 * Disabling the controls will disable validation.
 */
export function switchDisabledControlsByField2<
  T extends {[K in keyof T]: AbstractControl},
  S,
>(opts: {
  /**
   * Form Group
   */
  formGroup: FormGroup<T>;

  /**
   * Select / Switch Control
   */
  switchCtrl: FormControl<S>;

  /**
   * Sets of fields to activate, keyed by switch value
   */
  enabledControlsByValue: Map<S, (keyof T)[]>;
}) {
  const ctrl = (key: keyof T): AbstractControl =>
    (opts.formGroup.controls as any)[key];

  // Collect all affected controls
  const fields = [...new Set([...opts.enabledControlsByValue.values()].flat())];

  const updateControls = () => {
    // If parent form group is disabled, don't touch disabled / enabled state
    if (opts.formGroup.disabled) {
      return;
    }

    // Enable selected controls
    const keys = opts.enabledControlsByValue.get(opts.switchCtrl.value);
    keys
      ?.map((it) => {
        return it;
      })
      ?.map(ctrl)
      ?.forEach((control) => {
        control.enable();
      });

    // Disable other controls
    fields
      .filter((it) => !keys?.includes(it))
      ?.map((it) => {
        return it;
      })
      .map(ctrl)
      .forEach((control) => {
        control.disable();
      });
  };

  // Update now and on future value changes
  value$(opts.switchCtrl).subscribe(() => updateControls());
}

/**
 * Flatten nested Form Groups / Form Arrays into a single list of [path, control].
 *
 * Method for debugging nested angular forms
 *
 * @param form form group (or form array)
 */
export function flattenControls(
  form: FormGroup | FormArray,
): [string, AbstractControl][] {
  const results: [string, AbstractControl][] = [];

  const join = (path: string, element: string) =>
    path ? `${path}.${element}` : `${element}`;

  const iterate = (prefix: string, fg: FormGroup | FormArray) => {
    Object.entries(fg.controls).forEach(([key, ctrl]) => {
      const path = join(prefix, key);
      results.push([path, ctrl]);
      if (ctrl instanceof FormGroup || ctrl instanceof FormArray) {
        iterate(path, ctrl);
      }
    });
  };

  iterate('', form);

  return results;
}

/**
 * Control's value as observable that also emits current value.
 */
export function value$<T>(ctrl: AbstractControl<unknown>): Observable<T> {
  return concat(
    new Observable<T>((obs) => {
      obs.next(ctrl.value as T);
      obs.complete();
    }),
    ctrl.valueChanges as Observable<T>,
  );
}

/**
 * Control's status changes as observable that also emits current status.
 */
export function status$(ctrl: AbstractControl): Observable<FormControlStatus> {
  return concat(
    new Observable<FormControlStatus>((obs) => {
      obs.next(ctrl.status);
      obs.complete();
    }),
    ctrl.statusChanges,
  );
}
