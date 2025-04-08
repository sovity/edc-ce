/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useRouter} from 'next/navigation';
import {api} from '@/lib/api/client';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {useQuery} from '@tanstack/react-query';

export const useContractNegotiationStatus = (
  contractNegotiationId: string | null,
) => {
  const router = useRouter();
  return useQuery({
    queryKey: [queryKeys.contractNegotiation.key(), contractNegotiationId],
    queryFn: async () => {
      if (!contractNegotiationId) return null;
      return await api.uiApi.getContractNegotiation({contractNegotiationId});
    },
    enabled: !!contractNegotiationId,
    refetchOnMount: false,
    refetchOnReconnect: false,
    refetchOnWindowFocus: false,
    refetchInterval: (data) => {
      if (data?.contractAgreementId) {
        router.push(
          urls.contracts.detailPage(
            data.contractAgreementId,
            'contract-agreement',
          ),
        );
        return false;
      }
      return 500;
    },
  });
};
