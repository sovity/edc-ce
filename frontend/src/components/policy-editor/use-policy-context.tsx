/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {PolicyContext} from '@/components/policy-editor/core/policy-context';
import {type PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {byTranslation} from '@/lib/utils/translation-utils';
import {useConfig} from '@/lib/hooks/use-config';
import {getSovityPolicyVerbs} from '@/components/policy-editor/supported-sovity-policies';
import {getCatenaPolicyVerbs} from '@/components/policy-editor/supported-catena-policies';

export const usePolicyContext = (): PolicyContext => {
  const config = useConfig();

  const supportedPolicyVerbs: PolicyVerbConfig[] = [];

  if (config?.features?.includes('SOVITY_POLICIES')) {
    supportedPolicyVerbs.push(...getSovityPolicyVerbs());
  }

  if (config?.features?.includes('CATENA_POLICIES')) {
    supportedPolicyVerbs.push(...getCatenaPolicyVerbs());
  }

  const supportedPolicyOperators: PolicyOperatorConfig[] =
    getSupportedPolicyOperators();

  const supportedMultiExpressions: PolicyMultiExpressionConfig[] =
    getSupportedMultiExpressions();

  return new PolicyContext(
    supportedMultiExpressions,
    supportedPolicyOperators,
    supportedPolicyVerbs,
  );
};

function getSupportedPolicyOperators(): PolicyOperatorConfig[] {
  return [
    {
      id: 'EQ',
      title: byTranslation('General.Policies.Operators.eqTitle'),
      description: byTranslation('General.Policies.Operators.eqDescription'),
    },
    {
      id: 'NEQ',
      title: byTranslation('General.Policies.Operators.neqTitle'),
      description: byTranslation('General.Policies.Operators.neqDescription'),
    },
    {
      id: 'GEQ',
      title: byTranslation('General.Policies.Operators.geqTitle'),
      description: byTranslation('General.Policies.Operators.geqDescription'),
    },
    {
      id: 'GT',
      title: byTranslation('General.Policies.Operators.gtTitle'),
      description: byTranslation('General.Policies.Operators.gtDescription'),
    },
    {
      id: 'LEQ',
      title: byTranslation('General.Policies.Operators.leqTitle'),
      description: byTranslation('General.Policies.Operators.leqDescription'),
    },
    {
      id: 'LT',
      title: byTranslation('General.Policies.Operators.ltTitle'),
      description: byTranslation('General.Policies.Operators.ltDescription'),
    },
    {
      id: 'IN',
      title: byTranslation('General.Policies.Operators.inTitle'),
      description: byTranslation('General.Policies.Operators.inDescription'),
    },
    {
      id: 'HAS_PART',
      title: byTranslation('General.Policies.Operators.hasPartTitle'),
      description: byTranslation(
        'General.Policies.Operators.hasPartDescription',
      ),
    },
    {
      id: 'IS_A',
      title: byTranslation('General.Policies.Operators.isATitle'),
      description: byTranslation('General.Policies.Operators.isADescription'),
    },
    {
      id: 'IS_NONE_OF',
      title: byTranslation('General.Policies.Operators.isNoneOfTitle'),
      description: byTranslation(
        'General.Policies.Operators.isNoneOfDescription',
      ),
    },
    {
      id: 'IS_ANY_OF',
      title: byTranslation('General.Policies.Operators.isAnyOfTitle'),
      description: byTranslation(
        'General.Policies.Operators.isAnyOfDescription',
      ),
    },
    {
      id: 'IS_ALL_OF',
      title: byTranslation('General.Policies.Operators.isAllOfTitle'),
      description: byTranslation(
        'General.Policies.Operators.isAllOfDescription',
      ),
    },
  ];
}

function getSupportedMultiExpressions(): PolicyMultiExpressionConfig[] {
  return [
    {
      expressionType: 'AND',
      title: byTranslation('General.Policies.MultiExpressions.andTitle'),
      description: byTranslation(
        'General.Policies.MultiExpressions.andDescription',
      ),
    },
    {
      expressionType: 'OR',
      title: byTranslation('General.Policies.MultiExpressions.orTitle'),
      description: byTranslation(
        'General.Policies.MultiExpressions.orDescription',
      ),
    },
    {
      expressionType: 'XONE',
      title: byTranslation('General.Policies.MultiExpressions.xoneTitle'),
      description: byTranslation(
        'General.Policies.MultiExpressions.xoneDescription',
      ),
    },
  ];
}
