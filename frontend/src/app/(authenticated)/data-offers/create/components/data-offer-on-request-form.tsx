/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InputField from '@/components/form/input-field';
import {type DataOfferType} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';

export const dataOfferOnRequestSchema = z.object({
  offerType: z.literal('ON_REQUEST' satisfies DataOfferType),
  contactEmail: z.string().email(),
  contactPreferredEmailSubject: z.string().min(1).max(128),
});

export type DataOfferOnRequestFormValue = z.infer<
  typeof dataOfferOnRequestSchema
>;

export const DataOfferOnRequestForm = ({
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

  const value = form.watch(formKeyDataOfferType) as DataOfferOnRequestFormValue;

  return (
    value.offerType === 'ON_REQUEST' && (
      <>
        {/* Contact E-Mail for On Request Assets */}
        <InputField
          control={form.control}
          name={fieldKey('contactEmail')}
          placeholder={t('Pages.DataOfferCreate.contact_email_placeholder')}
          label={t('Pages.DataOfferCreate.contact_email')}
          tooltip={t('Pages.DataOfferCreate.contact_email_tooltip')}
          isRequired
        />

        {/* Contact E-Mail Subject for On Request Assets */}
        <InputField
          control={form.control}
          name={fieldKey('contactPreferredEmailSubject')}
          placeholder={t('Pages.DataOfferCreate.email_subject_placeholder')}
          label={t('Pages.DataOfferCreate.email_subject')}
          tooltip={t('Pages.DataOfferCreate.email_subject_tooltip')}
          isRequired
        />
      </>
    )
  );
};
