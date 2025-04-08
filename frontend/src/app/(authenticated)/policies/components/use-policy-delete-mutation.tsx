/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useToast} from '@/components/ui/use-toast';
import {api} from '@/lib/api/client';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {queryKeys} from '@/lib/queryKeys';
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export const usePolicyDeleteMutation = (dismiss: () => void) => {
  const invalidatePolicyDefinition = useInvalidateId(queryKeys.policies);
  const {toast} = useToast();
  const t = useTranslations();

  return useMutation({
    mutationFn: async (policyId: string) =>
      await api.uiApi.deletePolicyDefinition({
        policyId,
      }),
    onSuccess: async ({id}) => {
      toast({
        title: t('General.success'),
        description: `${t('Pages.PolicyList.policyRemoved')}`,
      });
      dismiss();
      await invalidatePolicyDefinition(id);
    },
    onError: (error: Error) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
