/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {PolicyContext} from '@/components/policy-editor/core/policy-context';
import {type PolicyMultiExpressionConfig} from '@/components/policy-editor/model/policy-multi-expression-config';
import {type PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {type PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {byTranslation} from '@/lib/utils/translation-utils';

export const sovityDataspacePolicyContext = (): PolicyContext => {
  const supportedPolicyVerbs: PolicyVerbConfig[] = [
    {
      operandLeftId: 'REFERRING_CONNECTOR',
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.referringConnectorOperandRightPlaceholder',
      ),

      supportedOperators: ['EQ', 'IN'],
      valueType: 'STRING_LIST_WITH_COMMA_SUPPORT',
    },
    {
      operandLeftId: 'POLICY_EVALUATION_TIME',
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.evaluationTimeOperandRightPlaceholder',
      ),

      supportedOperators: ['GEQ', 'LEQ', 'GT', 'LT'],
      valueType: 'DATETIME_TRUNCATE_TO_DATE',
    },
  ];

  const supportedPolicyOperators: PolicyOperatorConfig[] = [
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

  const supportedMultiExpressions: PolicyMultiExpressionConfig[] = [
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

  return new PolicyContext(
    supportedMultiExpressions,
    supportedPolicyOperators,
    supportedPolicyVerbs,
  );
};
