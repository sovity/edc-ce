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
import {buildUiAssetCreateRequest} from '@/app/(authenticated)/data-offers/create/components/ui-asset-form-mapper';
import {api} from '@/lib/api/client';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {
  type IdResponseDto,
  type UiPolicyExpression,
} from '@sovity.de/edc-client';
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';

export interface CreateDataOfferParams {
  formValue: DataOfferCreateFormModel;
  expression: UiPolicyExpression;
}

export const useDataOfferCreateMutation = () => {
  const invalidatePolicy = useInvalidateId(queryKeys.policies);
  const invalidateAsset = useInvalidateId(queryKeys.assets);
  const invalidateDataOffer = useInvalidateId(queryKeys.dataOffers);
  const router = useRouter();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationKey: ['createDataOffer'],
    mutationFn: async ({
      formValue,
      expression,
    }: CreateDataOfferParams): Promise<IdResponseDto> => {
      const asset = buildUiAssetCreateRequest(formValue);

      return await api.uiApi.createDataOffer({
        dataOfferCreateRequest: {
          publishType: formValue.publishing.mode,
          policyExpression:
            formValue.publishing.mode === 'PUBLISH_RESTRICTED'
              ? expression
              : undefined,
          asset,
        },
      });
    },
    onSuccess: async ({id}, {formValue}) => {
      toast({
        title: t('General.success'),
        description: t('Pages.DataOfferCreate.submitSuccess'),
      });

      switch (formValue.publishing.mode) {
        case 'PUBLISH_RESTRICTED':
          await invalidateAsset(id);
          await invalidateDataOffer(id);
          await invalidatePolicy(id);
          router.push(urls.dataOffers.listPage());
          break;
        case 'PUBLISH_UNRESTRICTED':
          await invalidateAsset(id);
          await invalidateDataOffer(id);
          router.push(urls.dataOffers.listPage());
          break;
        case 'DONT_PUBLISH':
          await invalidateAsset(id);
          router.push(urls.assets.listPage());
          break;
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
