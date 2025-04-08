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

const publishDataOfferSchema = (t: TranslateFn) =>
  z.object({
    dataOfferId: z
      .string()
      .min(1)
      .max(128)
      .regex(/^[^\s:]*$/, {
        message: t('General.dataOfferIdValidationWhitespacesColons'),
      }),
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

  return {
    form: useForm<PublishDataOfferFormValue>({
      mode: 'onTouched',
      resolver: zodResolver(publishDataOfferSchema(t)),
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
