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
import {type InitiateTransferFormValue} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-schema';
import {buildDataAddressProperties} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-request-mapper';
import {api} from '@/lib/api/client';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {
  type ContractAgreementCard,
  type IdResponseDto,
} from '@sovity.de/edc-client';
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export interface InitiateTransferParams {
  contractAgreement: ContractAgreementCard;
  formValue: InitiateTransferFormValue;
}

export const useContractTransferMutation = () => {
  const invalidateContract = useInvalidateId(queryKeys.contracts);
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationFn: async ({
      contractAgreement,
      formValue,
    }: InitiateTransferParams): Promise<IdResponseDto> => {
      return await api.uiApi.initiateTransfer({
        initiateTransferRequest: {
          contractAgreementId: contractAgreement.contractAgreementId,
          transferType: 'HttpData-PUSH',
          dataSinkProperties: buildDataAddressProperties(formValue),
          transferProcessProperties: {},
        },
      });
    },
    onSuccess: async ({}, {contractAgreement}) => {
      toast({
        title: t('General.success'),
        description: t('Pages.InitiateTransfer.submitSuccess'),
      });
      await invalidateContract(contractAgreement.contractAgreementId);
      router.push(
        urls.contracts.detailPage(
          contractAgreement.contractAgreementId,
          'transfers',
        ),
      );
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
