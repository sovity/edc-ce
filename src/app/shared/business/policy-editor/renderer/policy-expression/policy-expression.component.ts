import {Component, HostBinding, Input} from '@angular/core';
import {PolicyExpressionMapped} from '../../model/policy-expression-mapped';

@Component({
  selector: 'policy-expression',
  templateUrl: './policy-expression.component.html',
})
export class PolicyExpressionComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.justify-stretch')
  cls = true;

  @Input()
  expression!: PolicyExpressionMapped;
}
