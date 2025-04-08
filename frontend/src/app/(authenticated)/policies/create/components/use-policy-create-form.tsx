/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {policyEditorFormSchema} from '@/components/policy-editor/editor/policy-editor-form-schema';
import {zodResolver} from '@hookform/resolvers/zod';
import {useForm} from 'react-hook-form';
import {z} from 'zod';

export const policyCreateForm = z.object({
  policyDefinitionId: z.string().min(1),
  policy: policyEditorFormSchema,
});

export type PolicyCreateFormValue = z.infer<typeof policyCreateForm>;

export const usePolicyCreateForm = () => {
  return {
    form: useForm<PolicyCreateFormValue>({
      mode: 'onTouched',
      resolver: zodResolver(policyCreateForm),
      defaultValues: {
        policyDefinitionId: '',
        policy: {},
      },
    }),
    schema: policyCreateForm,
  };
};
