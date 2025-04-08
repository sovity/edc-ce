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

export const useDataOfferDeleteMutation = (dismiss: () => void) => {
  const invalidateDataOffers = useInvalidateId(queryKeys.dataOffers);
  const {toast} = useToast();
  const t = useTranslations();

  return useMutation({
    mutationFn: async (contractDefinitionId: string) => {
      return await api.uiApi.deleteContractDefinition({
        contractDefinitionId,
      });
    },
    onSuccess: async ({id}) => {
      toast({
        title: t('General.success'),
        description: `${t('Pages.DataOfferList.dataOfferRemoved')}`,
      });
      dismiss();
      await invalidateDataOffers(id);
    },
    onError: (error: Error) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
