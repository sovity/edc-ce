/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {byTranslation} from '@/lib/utils/translation-utils';

export function getCatenaPolicyVerbs(): PolicyVerbConfig[] {
  return [
    {
      operandLeftIds: [
        'https://w3id.org/tractusx/v0.0.1/ns/BusinessPartnerGroup',
        'https://w3id.org/edc/v0.0.1/ns/BusinessPartnerGroup',
        'https://w3id.org/catenax/policy/BusinessPartnerGroup',
        'https://w3id.org/catenax/2025/9/policy/BusinessPartnerGroup', // exists starting 25.09
      ],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.bpnGroupOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.bpnGroupOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.bpnGroupOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.bpnGroupOperandRightPlaceholder',
      ),
      supportedOperators: [
        'EQ',
        'NEQ',
        'IN',
        'IS_ALL_OF',
        'IS_ANY_OF',
        'IS_NONE_OF',
      ],
      valueType: 'STRING_LIST_CATENA_STYLE',
    },
    {
      operandLeftIds: [
        'https://w3id.org/tractusx/v0.0.1/ns/BusinessPartnerNumber',
        'https://w3id.org/edc/v0.0.1/ns/BusinessPartnerNumber',
        'https://w3id.org/catenax/policy/BusinessPartnerNumber',
        'https://w3id.org/catenax/2025/9/policy/BusinessPartnerNumber', // exists starting 25.09
        'BusinessPartnerNumber',
      ],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.bpnOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.bpnOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.bpnOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.bpnOperandRightPlaceholder',
      ),
      supportedOperators: [
        'EQ',
        'NEQ',
        'HAS_PART',
        'IN',
        'IS_A',
        'IS_ANY_OF',
        'IS_NONE_OF',
      ],
      valueType: 'STRING_LIST_CATENA_STYLE',
    },
    {
      operandLeftIds: [
        'inForceDate',
        'https://w3id.org/edc/v0.0.1/ns/inForceDate',
      ],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.inForceDateOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.inForceDateOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.inForceDateOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.inForceDateOperandRightPlaceholder',
      ),

      supportedOperators: ['GEQ', 'LEQ', 'GT', 'LT'],
      valueType: 'IN_FORCE_DATE',
    },
    {
      operandLeftIds: [
        'https://w3id.org/catenax/policy/FrameworkAgreement',
        'https://w3id.org/catenax/2025/9/policy/FrameworkAgreement', // exists starting 25.09
      ],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.frameworkAgreementOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.frameworkAgreementOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.frameworkAgreementOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.frameworkAgreementOperandRightPlaceholder',
      ),
      supportedOperators: ['EQ', 'NEQ'],
      valueType: 'STRING',
    },
    {
      operandLeftIds: [
        'https://w3id.org/catenax/policy/Membership',
        'https://w3id.org/catenax/2025/9/policy/Membership', // exists starting 25.09
        'Membership',
      ],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.membershipOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.membershipOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.membershipOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.membershipOperandRightPlaceholder',
      ),

      supportedOperators: ['EQ'],
      valueType: 'STRING',
    },
    {
      operandLeftIds: [
        'https://w3id.org/catenax/policy/UsagePurpose',
        'https://w3id.org/catenax/2025/9/policy/UsagePurpose', // exists starting 25.09
      ],
      operandLeftTitle: byTranslation(
        'General.Policies.Verbs.usagePurposeOperandLeftTitle',
      ),
      operandLeftDescription: byTranslation(
        'General.Policies.Verbs.usagePurposeOperandLeftDescription',
      ),

      operandRightTitle: byTranslation(
        'General.Policies.Verbs.usagePurposeOperandRightTitle',
      ),
      operandRightPlaceholder: byTranslation(
        'General.Policies.Verbs.usagePurposeOperandRightPlaceholder',
      ),
      supportedOperators: ['EQ', 'NEQ'],
      valueType: 'STRING',
    },
  ];
}
