/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {policyEditorFormSchema} from '@/components/policy-editor/editor/policy-editor-form-schema';
import {api} from '@/lib/api/client';
import {
  type CancellablePromise,
  useCancellablePromise,
} from '@/lib/hooks/use-cancellable-promise';
import {allowedIdRegex, invalidIdError} from '@/lib/utils/id-utils';
import {zodResolver} from '@hookform/resolvers/zod';
import {useTranslations} from 'next-intl';
import {useForm} from 'react-hook-form';
import {z} from 'zod';

export const policyCreateForm = (
  withCancellation: CancellablePromise,
  policyIdValidationMessage: string,
) =>
  z.object({
    policyDefinitionId: z
      .string()
      .min(1)
      .regex(allowedIdRegex, {
        message: invalidIdError,
      })
      .refine(
        async (policyId) => {
          const {available} = await withCancellation(
            api.uiApi.isPolicyIdAvailable({policyId}),
          );
          return available;
        },
        {message: policyIdValidationMessage},
      ),
    policy: policyEditorFormSchema,
  });

export type PolicyCreateFormValue = z.infer<
  ReturnType<typeof policyCreateForm>
>;

export const usePolicyCreateForm = () => {
  const withCancellation = useCancellablePromise();
  const policyIdValidationMessage = useTranslations()(
    'General.Validators.policyIdTaken',
  );

  return {
    form: useForm<PolicyCreateFormValue>({
      mode: 'onTouched',
      resolver: zodResolver(
        policyCreateForm(withCancellation, policyIdValidationMessage),
      ),
      defaultValues: {
        policyDefinitionId: '',
        policy: {},
      },
    }),
    schema: policyCreateForm,
  };
};
