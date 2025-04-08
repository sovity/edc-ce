/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import SelectField from '@/components/form/select-field';
import {
  DataOfferLiveCustomForm,
  dataOfferLiveCustomSchema,
} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-custom-form';
import {
  DataOfferLiveHttpForm,
  dataOfferLiveHttpSchema,
} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-http-form';
import {
  type DataOfferLiveType,
  type DataOfferType,
} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';

export const dataOfferLiveSchema = z.object({
  offerType: z.literal('LIVE' satisfies DataOfferType),
  live: z.discriminatedUnion('offerLiveType', [
    dataOfferLiveCustomSchema,
    dataOfferLiveHttpSchema,
  ]),
});

export type DataOfferLiveFormValue = z.infer<typeof dataOfferLiveSchema>;

export const DataOfferLiveForm = ({
  form,
  formKeyDataOfferType,
}: {
  form: UseFormReturn<any>;
  formKeyDataOfferType: string;
}) => {
  const t = useTranslations();

  const fieldKey = (key: string): string =>
    formKeyDataOfferType === ''
      ? formKeyDataOfferType
      : `${formKeyDataOfferType}.${key}`;

  const value = form.watch(formKeyDataOfferType) as DataOfferLiveFormValue;

  return (
    value.offerType === 'LIVE' && (
      <>
        {/* Data Address Type */}
        <SelectField
          control={form.control}
          name={fieldKey('live.offerLiveType')}
          label={t('Pages.DataOfferCreate.type')}
          items={[
            {
              id: 'CUSTOM_JSON' satisfies DataOfferLiveType,
              label: t('Pages.DataOfferCreate.dataSourceTypeCustom'),
            },
            {
              id: 'HTTP' satisfies DataOfferLiveType,
              label: t('Pages.DataOfferCreate.dataSourceTypeHttp'),
            },
          ]}
          placeholder={''}
        />

        <DataOfferLiveCustomForm
          form={form}
          formKeyDataOfferTypeLive={fieldKey('live')}
        />

        <DataOfferLiveHttpForm
          form={form}
          formKeyDataOfferTypeLive={fieldKey('live')}
        />
      </>
    )
  );
};
