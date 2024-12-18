import {Component, Input} from '@angular/core';
import {PolicyExpressionMapped} from '../../model/policy-expression-mapped';

@Component({
  selector: 'app-policy-renderer',
  templateUrl: './policy-renderer.component.html',
})
export class PolicyRendererComponent {
  @Input()
  expression!: PolicyExpressionMapped;

  @Input()
  errors: string[] = [];
}
