/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useTranslations} from 'next-intl';
import {type DataOfferPolicyCreateType} from '@sovity.de/edc-client';
import {type Control} from 'react-hook-form';
import RadioGroupField from '@/components/form/radio-group-field';

export const PolicyInputTypeSelect = ({
  name,
  formControl,
}: {
  name: string;
  formControl: Control<any, any>;
}) => {
  const t = useTranslations();
  return (
    <RadioGroupField
      control={formControl}
      name={name}
      label={t('General.PolicyInputType.label')}
      items={[
        {
          id: 'POLICY_EXPRESSION' satisfies DataOfferPolicyCreateType,
          label: t('General.PolicyInputType.policyExpressionLabel'),
          description: t('General.PolicyInputType.policyExpressionDescription'),
        },
        {
          id: 'POLICY_JSON_LD' satisfies DataOfferPolicyCreateType,
          label: t('General.PolicyInputType.policyJsonLdLabel'),
          description: t('General.PolicyInputType.policyJsonLdDescription'),
        },
      ]}
    />
  );
};
