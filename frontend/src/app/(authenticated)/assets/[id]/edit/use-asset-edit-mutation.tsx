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
import {type DataOfferCreateFormModel} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {buildUiAssetEditRequest} from '@/app/(authenticated)/data-offers/create/components/ui-asset-form-mapper';
import {api} from '@/lib/api/client';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {type IdResponseDto} from '@sovity.de/edc-client';
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export interface EditAssetMutationParams {
  assetId: string;
  formValue: DataOfferCreateFormModel;
}

export const useAssetEditMutation = () => {
  const invalidateAsset = useInvalidateId(queryKeys.assets);
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationKey: ['editAsset'],
    mutationFn: async ({
      assetId,
      formValue,
    }: EditAssetMutationParams): Promise<IdResponseDto> => {
      const uiAssetEditRequest = buildUiAssetEditRequest(formValue);

      return await api.uiApi.editAsset({
        assetId: assetId,
        uiAssetEditRequest,
      });
    },
    onSuccess: async ({id}) => {
      toast({
        title: t('General.success'),
        description: t('Pages.AssetEditPage.submitSuccess'),
      });

      await invalidateAsset(id);
      router.push(urls.assets.detailPage(id));
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
