/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useRouter} from 'next/navigation';
import {useToast} from '@/components/ui/use-toast';
import {api} from '@/lib/api/client';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {
  type PolicyDefinitionCreateDto,
  type PolicyDefinitionCreateFromJsonLdDto,
} from '@sovity.de/edc-client';
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export const usePolicyCreateMutation = () => {
  const invalidatePolicy = useInvalidateId(queryKeys.policies);
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  const mutationFn = async (
    dto: PolicyDefinitionCreateDto | PolicyDefinitionCreateFromJsonLdDto,
  ) => {
    return 'policyExpression' in dto
      ? await api.uiApi.createPolicyDefinitionV2({
          policyDefinitionCreateDto: dto,
        })
      : await api.uiApi.createPolicyDefinitionFromJsonLd({
          policyDefinitionCreateFromJsonLdDto: dto,
        });
  };

  return useMutation({
    mutationKey: ['createPolicy'],
    mutationFn,
    onSuccess: async ({id}) => {
      toast({
        title: t('General.success'),
        description: t('Pages.PolicyCreate.submitSuccess'),
      });

      await invalidatePolicy(id);
      router.push(urls.policies.listPage());
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
