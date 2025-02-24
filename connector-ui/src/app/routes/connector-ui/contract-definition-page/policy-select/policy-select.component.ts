import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PolicyDefinitionDto} from '@sovity.de/edc-client';

@Component({
  selector: 'policy-select',
  templateUrl: 'policy-select.component.html',
})
export class PolicySelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<PolicyDefinitionDto | null>;

  @Input()
  policies: PolicyDefinitionDto[] = [];

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  compareWith(
    a: PolicyDefinitionDto | null,
    b: PolicyDefinitionDto | null,
  ): boolean {
    return this.policyId(a) === this.policyId(b);
  }

  policyId(a: PolicyDefinitionDto | null): string | null {
    return a?.policyDefinitionId ?? null;
  }
}
