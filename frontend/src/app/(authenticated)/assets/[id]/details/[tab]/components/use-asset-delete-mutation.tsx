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
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export const useAssetDeleteMutation = (dismiss: () => void) => {
  const {toast} = useToast();
  const t = useTranslations();
  const queryClient = useQueryClient();
  const router = useRouter();

  return useMutation({
    mutationFn: async (assetId: string) => {
      return await api.uiApi.deleteAsset({
        assetId: assetId,
      });
    },
    onSuccess: async () => {
      toast({
        title: t('General.success'),
        description: `${t('Pages.AssetDetails.assetRemoved')}`,
      });
      router.push(urls.assets.listPage());
      dismiss();
      await queryClient.invalidateQueries({
        queryKey: queryKeys.assets.key(),
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
