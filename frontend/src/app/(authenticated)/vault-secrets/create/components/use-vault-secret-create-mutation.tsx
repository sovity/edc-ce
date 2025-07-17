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
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {type IdResponseDto} from '@sovity.de/edc-client';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';
import {type CreateVaultSecretForm} from './use-create-vault-secret-form';

export const useVaultSecretCreateMutation = (redirectOnSuccess: boolean) => {
  const queryClient = useQueryClient();
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationKey: ['createVaultSecret'],
    mutationFn: async (
      formValue: CreateVaultSecretForm,
    ): Promise<IdResponseDto> => {
      return await api.uiApi.createVaultSecret({
        vaultSecretCreateSubmit: {
          key: formValue.key,
          value: formValue.value,
          description: formValue.description,
        },
      });
    },
    onSuccess: async () => {
      toast({
        title: t('General.success'),
        description: t('Pages.VaultSecretsCreate.submitSuccess'),
      });

      await queryClient.invalidateQueries(queryKeys.vaultSecrets.listPage());
      if (redirectOnSuccess) {
        router.push(urls.vaultSecrets.listPage());
      }
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
