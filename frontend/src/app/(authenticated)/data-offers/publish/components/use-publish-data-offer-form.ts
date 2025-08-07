/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {zodResolver} from '@hookform/resolvers/zod';
import {useTranslations} from 'next-intl';
import {useForm} from 'react-hook-form';
import z from 'zod';
import {type TranslateFn} from '@/lib/utils/translation-utils';
import {api} from '@/lib/api/client';
import {
  type CancellablePromise,
  useCancellablePromise,
} from '@/lib/hooks/use-cancellable-promise';
import {allowedIdRegex, invalidIdError} from '@/lib/utils/id-utils';

const publishDataOfferSchema = (
  t: TranslateFn,
  withCancellation: CancellablePromise,
) =>
  z.object({
    dataOfferId: z
      .string()
      .min(1)
      .max(128)
      .regex(allowedIdRegex, {
        message: invalidIdError,
      })
      .refine(
        async (dataOfferId) => {
          const response = await withCancellation(
            Promise.all([
              api.uiApi.isContractDefinitionIdAvailable({
                contractDefinitionId: dataOfferId,
              }),
              api.uiApi.isAssetIdAvailable({
                assetId: dataOfferId,
              }),
              api.uiApi.isPolicyIdAvailable({
                policyId: dataOfferId,
              }),
            ]),
          );
          return response.every((r) => r.available);
        },
        {
          message: t('General.Validators.dataOfferIdTaken'),
        },
      ),
    assetIdList: z.array(z.string()).min(1, {
      message: t('General.validationSelectOneAsset'),
    }),
    accessPolicy: z.string().min(1),
    contractPolicy: z.string().min(1),
  });

export type PublishDataOfferFormValue = z.infer<
  ReturnType<typeof publishDataOfferSchema>
>;

export const usePublishDataOfferForm = () => {
  const t = useTranslations();
  const withCancellation = useCancellablePromise();

  return {
    form: useForm<PublishDataOfferFormValue>({
      mode: 'onTouched',
      resolver: zodResolver(publishDataOfferSchema(t, withCancellation)),
      defaultValues: {
        dataOfferId: '',
        assetIdList: [],
        accessPolicy: '',
        contractPolicy: '',
      },
    }),
    schema: publishDataOfferSchema,
  };
};
