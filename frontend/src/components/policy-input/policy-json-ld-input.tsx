/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {Control} from 'react-hook-form';
import {useTranslations} from 'next-intl';
import TextareaField from '@/components/form/textarea-field';
import {POLICY_JSON_LD_INPUT_DEFAULT_VALUE} from '@/lib/policy-constants';

export const PolicyJsonLdInput = ({
  name,
  formControl,
}: {
  name: string;
  formControl: Control<any, any>;
}) => {
  const t = useTranslations();

  return (
    <TextareaField
      control={formControl}
      name={name}
      placeholder={POLICY_JSON_LD_INPUT_DEFAULT_VALUE}
      label={t('General.PolicyInputType.policyJsonLdTextareaLabel')}
      tooltip={t('General.PolicyInputType.policyJsonLdTextareaTooltip')}
      isRequired
    />
  );
};
