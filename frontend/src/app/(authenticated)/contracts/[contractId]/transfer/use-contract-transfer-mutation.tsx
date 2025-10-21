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
import {api} from '@/lib/api/client';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import type {
  IdResponseDto,
  UiDataSinkHttpDataPush,
  UiDataSinkHttpDataPushMethod,
  UiHttpPushAuth,
  UiInitiateTransferRequest,
} from '@sovity.de/edc-client';
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';
import {type InitiateTransferHttpFormValue} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-http-form';
import {buildHttpHeaders} from '@/app/(authenticated)/data-offers/create/components/ui-data-source-mapper';

export interface InitiateTransferParams {
  contractAgreementId: string;
  formValue: InitiateTransferFormValue;
}

export const useContractTransferMutation = () => {
  const invalidateContract = useInvalidateId(queryKeys.contracts);
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationFn: async ({
      contractAgreementId,
      formValue,
    }: InitiateTransferParams): Promise<IdResponseDto> => {
      return await api.uiApi.initiateTransferV2({
        uiInitiateTransferRequest: buildTransferRequest({
          contractAgreementId,
          formValue,
        }),
      });
    },
    onSuccess: async ({}, {contractAgreementId}) => {
      toast({
        title: t('General.success'),
        description: t('Pages.InitiateTransfer.submitSuccess'),
      });
      await invalidateContract(contractAgreementId);
      router.push(urls.contracts.detailPage(contractAgreementId, 'transfers'));
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};

const buildTransferRequest = ({
  contractAgreementId,
  formValue,
}: InitiateTransferParams): UiInitiateTransferRequest => {
  const transferType = formValue.transferType;
  if (transferType === 'CUSTOM_JSON') {
    return {
      type: 'CUSTOM',
      contractAgreementId,
      customTransferType: formValue.customTransferType,
      customTransferPrivateProperties: JSON.parse(
        formValue.transferPropertiesJson,
      ) as Record<string, string>,
      customDataSinkProperties: JSON.parse(formValue.dataAddressJson) as Record<
        string,
        string
      >,
    };
  }
  if (transferType === 'HTTP') {
    // HTTP PUSH
    return {
      type: 'HTTP_DATA_PUSH',
      contractAgreementId,
      httpDataPush: buildHttpPushTransferRequest(formValue),
    };
  }

  throw new Error(`Unsupported transfer type: ${JSON.stringify(formValue)}`);
};

const buildHttpPushTransferRequest = (
  formValue: InitiateTransferHttpFormValue,
): UiDataSinkHttpDataPush => {
  return {
    baseUrl: formValue.httpUrl,
    queryString: '',
    method: formValue.httpMethod as UiDataSinkHttpDataPushMethod,
    headers: buildHttpHeaders(formValue.httpAdditionalHeaders ?? []),
    auth: buildHttpPushAuth(formValue.auth) ?? undefined,
  };
};

const buildHttpPushAuth = (
  auth: InitiateTransferHttpFormValue['auth'],
): UiHttpPushAuth | null => {
  if (auth.type === 'VAULT_SECRET') {
    return {
      type: 'API_KEY',
      apiKey: {
        headerName: auth.headerName,
        vaultKey: auth.headerSecretName,
      },
    };
  }

  if (auth.type === 'BASIC') {
    return {
      type: 'BASIC',
      basic: {
        username: auth.username,
        password: auth.password,
      },
    };
  }

  return null;
};
