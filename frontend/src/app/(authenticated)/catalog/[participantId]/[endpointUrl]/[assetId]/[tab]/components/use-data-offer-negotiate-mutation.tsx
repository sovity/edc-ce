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
import {
  type ContractNegotiationRequest,
  type UiContractNegotiation,
} from '@/lib/api/client/generated';
import {queryKeys} from '@/lib/queryKeys';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export const useDataOfferNegotiateMutation = (
  onSuccess: (data: UiContractNegotiation) => void,
) => {
  const queryClient = useQueryClient();
  const {toast} = useToast();
  const t = useTranslations();

  return useMutation({
    mutationFn: async (data: ContractNegotiationRequest) =>
      await api.uiApi.initiateContractNegotiation({
        contractNegotiationRequest: data,
      }),
    onSuccess: async (data, {assetId, counterPartyId, counterPartyAddress}) => {
      onSuccess(data);
      await queryClient.invalidateQueries(
        queryKeys.catalog.listPage(counterPartyId, counterPartyAddress),
      );
      await queryClient.invalidateQueries(
        queryKeys.catalog.dataOffer(
          counterPartyId,
          counterPartyAddress,
          assetId,
        ),
      );
    },
    onError: (error: Error) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
