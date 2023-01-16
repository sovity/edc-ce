import {AbstractControl, FormControl, FormGroup, ValidatorFn} from "@angular/forms";


/**
 * Apply validators depending on selected option.
 *
 * Use this when a select switches entire parts of the form.
 */
export function switchValidation<T extends { [K in keyof T]: AbstractControl; }, S extends string | number | symbol>(opts: {
  formGroup: FormGroup<T>,
  switchCtrl: FormControl<S>,
  validators: Record<S, Partial<Record<keyof T, ValidatorFn | ValidatorFn[]>>>
}) {
  const ctrl = (key: keyof T): AbstractControl => (opts.formGroup.controls as any)[key]

  // Collect all affected ctrls
  // When removing validators, only reset on those fields
  const allValidators = Object.values(opts.validators) as Partial<Record<keyof T, any>>[]
  const fields = [...new Set(allValidators.flatMap(it => Object.keys(it)))] as (keyof T)[]

  const updateDatasourceValidators = () => {
    // Remove all validators
    fields
      .map(ctrl)
      .forEach(control => {
        control.clearValidators()
        control.updateValueAndValidity()
      })

    // Add correct validators
    const validators = Object.entries(opts.validators[opts.switchCtrl.value]) as
      [keyof T, ValidatorFn | ValidatorFn[]][]
    validators.forEach(([key, validators]) => ctrl(key).setValidators(validators))
  }

  // Update now
  updateDatasourceValidators()

  // Update on dataAddressType changes
  opts.switchCtrl.valueChanges.subscribe(() => updateDatasourceValidators())
}
