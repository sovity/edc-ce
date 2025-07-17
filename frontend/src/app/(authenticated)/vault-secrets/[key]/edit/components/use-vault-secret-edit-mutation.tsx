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
import {type EditVaultSecretForm} from './use-edit-vault-secret-form';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';

export const useVaultSecretEditMutation = (key: string) => {
  const queryClient = useQueryClient();
  const invalidateVaultSecret = useInvalidateId(queryKeys.vaultSecrets);
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationKey: ['editVaultSecret'],
    mutationFn: async (
      formValue: EditVaultSecretForm,
    ): Promise<IdResponseDto> => {
      return await api.uiApi.editVaultSecret({
        key,
        vaultSecretEditSubmit: {
          value: formValue.editValue ? formValue.value : undefined,
          description: formValue.description,
        },
      });
    },
    onSuccess: async () => {
      toast({
        title: t('General.success'),
        description: t('Pages.VaultSecretsEdit.submitSuccess'),
      });

      await invalidateVaultSecret(key);
      await queryClient.invalidateQueries(queryKeys.vaultSecrets.listPage());
      router.push(urls.vaultSecrets.listPage());
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
