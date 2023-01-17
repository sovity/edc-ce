import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PolicyDefinition} from 'src/modules/edc-dmgmt-client';

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

  policyId(a: PolicyDefinition | null): string {
    // EDC Milestone 7 changed PolicyDefinition.id to PolicyDefinition.uid
    // This is a temporary workaround
    return a?.id || (a as any)?.uid || 'no-policy-id-found';
  }
}
