import {Injectable} from '@angular/core';
import {UiPolicy} from '@sovity.de/edc-client';
import {PolicyCardBuilder} from '../../../routes/connector-ui/policy-definition-page/policy-cards/policy-card-builder';
import {JsonDialogService} from '../../json-dialog/json-dialog/json-dialog.service';
import {PropertyGridField} from '../../property-grid/property-grid/property-grid-field';

@Injectable()
export class PolicyPropertyFieldBuilder {
  constructor(
    private policyCardBuilder: PolicyCardBuilder,
    private jsonDialogService: JsonDialogService,
  ) {}

  buildPolicyPropertyFields(
    policy: UiPolicy,
    policyDetailDialogTitle: string,
    policyDetailDialogSubtitle: string,
  ): PropertyGridField[] {
    const constraints =
      this.policyCardBuilder.buildPolicyCardConstraints(policy);
    const irregularities = policy.errors;
    return [
      {
        icon: 'policy',
        label: 'Policy',
        additionalClasses: 'whitespace-pre-line',
        text: [
          ...constraints.map((it) => `${it.left} ${it.operator} ${it.right}`),
          ...irregularities,
        ].join('\n'),
      },
      {
        icon: 'policy',
        label: 'Policy JSON-LD',
        text: 'Show JSON-LD',
        onclick: () =>
          this.jsonDialogService.showJsonDetailDialog({
            title: policyDetailDialogTitle,
            subtitle: policyDetailDialogSubtitle,
            icon: 'policy',
            objectForJson: JSON.parse(policy.policyJsonLd),
          }),
      },
    ];
  }
}
