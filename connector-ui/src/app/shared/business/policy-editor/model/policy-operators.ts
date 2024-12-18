import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {OperatorDto} from '@sovity.de/edc-client';
import {LazyTranslation} from '../../../../core/utils/lazy-utils';
import {associateBy} from '../../../../core/utils/map-utils';

export interface PolicyOperatorConfig {
  id: OperatorDto;
  title: string;
  description: string;
}

@Injectable()
export class PolicyOperatorService {
  byId: LazyTranslation<Map<OperatorDto, PolicyOperatorConfig>>;

  constructor(private translateService: TranslateService) {
    this.byId = new LazyTranslation(this.translateService, () =>
      this.buildByIdMap(),
    );
  }

  getOperatorConfig(operator: OperatorDto): PolicyOperatorConfig {
    return (
      this.byId.getValue().get(operator) ??
      this.defaultPolicyOperatorConfig(operator)
    );
  }

  getSupportedPolicyOperators(): PolicyOperatorConfig[] {
    return [
      {
        id: 'EQ',
        title: '=',
        description: 'Equal to',
      },
      {
        id: 'NEQ',
        title: '≠',
        description: 'Not equal to',
      },
      {
        id: 'GEQ',
        title: '≥',
        description: 'Greater than or equal to',
      },
      {
        id: 'GT',
        title: '>',
        description: 'Greater than',
      },
      {
        id: 'LEQ',
        title: '≤',
        description: 'Less than or equal to',
      },
      {
        id: 'LT',
        title: '<',
        description: 'Less than',
      },
      {
        id: 'IN',
        title: 'IN',
        description: 'In',
      },
      {
        id: 'HAS_PART',
        title: 'HAS PART',
        description: 'Has Part',
      },
      {
        id: 'IS_A',
        title: 'IS A',
        description: 'Is a',
      },
      {
        id: 'IS_NONE_OF',
        title: 'IS NONE OF',
        description: 'Is none of',
      },
      {
        id: 'IS_ANY_OF',
        title: 'IS ANY OF',
        description: 'Is any of',
      },
      {
        id: 'IS_ALL_OF',
        title: 'IS ALL OF',
        description: 'Is all of',
      },
    ];
  }

  private buildByIdMap(): Map<OperatorDto, PolicyOperatorConfig> {
    return associateBy(this.getSupportedPolicyOperators(), (it) => it.id);
  }

  private defaultPolicyOperatorConfig(
    operator: OperatorDto,
  ): PolicyOperatorConfig {
    return {
      id: operator,
      title: operator,
      description: '',
    };
  }
}
