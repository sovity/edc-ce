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
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {type IdResponseDto} from '@sovity.de/edc-client';

export const useVaultSecretDeleteMutation = (dismiss: () => void) => {
  const {toast} = useToast();
  const t = useTranslations();
  const invalidateVaultSecret = useInvalidateId(queryKeys.vaultSecrets);

  return useMutation({
    mutationFn: async (key: string) => {
      return await api.uiApi.deleteVaultSecret({
        key,
      });
    },
    onSuccess: async (res: IdResponseDto) => {
      toast({
        title: t('General.success'),
        description: `${t('Pages.VaultSecretsList.secretRemoved')}`,
      });
      dismiss();
      await invalidateVaultSecret(res.id);
    },
    onError: (error: Error) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
