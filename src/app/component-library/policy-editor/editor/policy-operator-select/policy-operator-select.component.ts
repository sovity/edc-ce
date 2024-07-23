import {Component, HostBinding, Input} from '@angular/core';
import {UntypedFormControl} from '@angular/forms';
import {PolicyOperatorConfig} from '../../model/policy-operators';

@Component({
  selector: 'policy-operator-select',
  templateUrl: 'policy-operator-select.component.html',
})
export class PolicyOperatorSelectComponent {
  @Input()
  operators: PolicyOperatorConfig[] = [];

  @Input()
  control!: UntypedFormControl;

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  label = 'Operator';
}
