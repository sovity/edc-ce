/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import TextareaField from '@/components/form/textarea-field';
import {type InitiateTransferFormValue} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-schema';
import {type DataOfferLiveType} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {jsonString} from '@/lib/utils/zod/schema-utils';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';
import InputField from '@/components/form/input-field';

export const initiateTransferAzureBlobSchema = z.object({
  transferType: z.literal('AZURE_BLOB' satisfies DataOfferLiveType),
  customTransferType: z.string().min(1),
  transferPropertiesJson: jsonString(),
  dataAddressJson: jsonString(),
});

export type InitiateTransferAzureBlobForm = z.infer<
  typeof initiateTransferAzureBlobSchema
>;

export const InitiateTransferAzureBlobForm = ({
  form,
}: {
  form: UseFormReturn<InitiateTransferFormValue>;
}) => {
  const t = useTranslations();

  const value = form.watch();

  return (
    value.transferType === 'CUSTOM_JSON' && (
      <>
        {/* Custom Transfer Type */}
        <InputField
          className={'grow'}
          control={form.control}
          name={'customTransferType'}
          placeholder={'e.g. HttpData-PUSH'}
          label={t('General.customTransferType')}
          isRequired
        />
        {/* Custom Transfer Properties JSON */}
        <TextareaField
          control={form.control}
          name={'transferPropertiesJson'}
          placeholder="{...}"
          label={t('General.transferPropertiesJson')}
        />
        {/* Custom Data Address JSON */}
        <TextareaField
          control={form.control}
          name={'dataAddressJson'}
          placeholder='"dataAddress": { "type": ... }'
          label={t('General.customDataSinkJson')}
        />
      </>
    )
  );
};
