import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {
  PolicyDefinition,
  policyDefinitionId,
} from 'src/app/core/services/api/legacy-managent-api-client';

@Component({
  selector: 'policy-select',
  templateUrl: 'policy-select.component.html',
})
export class PolicySelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<PolicyDefinition | null>;

  @Input()
  policies: PolicyDefinition[] = [];

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  compareWith(a: PolicyDefinition | null, b: PolicyDefinition | null): boolean {
    return this.policyId(a) === this.policyId(b);
  }

  policyId(a: PolicyDefinition | null): string | null {
    return a ? policyDefinitionId(a) : null;
  }
}
