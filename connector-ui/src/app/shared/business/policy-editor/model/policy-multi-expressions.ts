import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {UiPolicyExpressionType} from '@sovity.de/edc-client';
import {LazyTranslation} from '../../../../core/utils/lazy-utils';
import {associateBy} from '../../../../core/utils/map-utils';

export interface PolicyMultiExpressionConfig {
  expressionType: UiPolicyExpressionType;
  title: string;
  description: string;
}

@Injectable()
export class PolicyMultiExpressionService {
  byId: LazyTranslation<
    Map<UiPolicyExpressionType, PolicyMultiExpressionConfig>
  >;

  constructor(private translateService: TranslateService) {
    this.byId = new LazyTranslation(this.translateService, () =>
      this.buildByIdMap(),
    );
  }

  getMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ): PolicyMultiExpressionConfig {
    return (
      this.byId.getValue().get(expressionType) ??
      this.getFallbackMultiExpressionConfig(expressionType)
    );
  }

  getSupportedMultiExpressions(): PolicyMultiExpressionConfig[] {
    return [
      {
        expressionType: 'AND',
        title: 'AND',
        description:
          'Conjunction of several expressions. Evaluates to true if and only if all child expressions are true',
      },
      {
        expressionType: 'OR',
        title: 'OR',
        description:
          'Disjunction of several expressions. Evaluates to true if and only if at least one child expression is true',
      },
      {
        expressionType: 'XONE',
        title: 'XONE',
        description:
          'XONE operation. Evaluates to true if and only if exactly one child expression is true',
      },
    ];
  }

  private buildByIdMap(): Map<
    UiPolicyExpressionType,
    PolicyMultiExpressionConfig
  > {
    return associateBy(
      this.getSupportedMultiExpressions(),
      (it) => it.expressionType,
    );
  }

  private getFallbackMultiExpressionConfig(
    expressionType: UiPolicyExpressionType,
  ) {
    return {
      expressionType,
      title: expressionType,
      description: '',
    };
  }
}
