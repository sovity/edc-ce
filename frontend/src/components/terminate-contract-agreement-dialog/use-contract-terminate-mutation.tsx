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
import {queryKeys} from '@/lib/queryKeys';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

interface TerminateContractInput {
  contractAgreementId: string;
  reason: string;
  detailedReason: string;
}

export const useContractTerminateMutation = (dismiss: () => void) => {
  const {toast} = useToast();
  const t = useTranslations();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      contractAgreementId,
      reason,
      detailedReason,
    }: TerminateContractInput) => {
      return await api.uiApi.terminateContractAgreement({
        contractAgreementId: contractAgreementId,
        contractTerminationRequest: {
          reason: reason,
          detail: detailedReason,
        },
      });
    },
    onSuccess: async () => {
      toast({
        title: t('General.success'),
        description: `${t('General.successfullyTerminated')}`,
      });
      dismiss();
      await queryClient.invalidateQueries({
        queryKey: queryKeys.contracts.key(),
      });
    },
    onError: (error: Error) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
