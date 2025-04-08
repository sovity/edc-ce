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

export const initiateTransferCustomSchema = z.object({
  transferType: z.literal('CUSTOM_JSON' satisfies DataOfferLiveType),
  dataAddressJson: jsonString(),
});

export type InitiateTransferCustomFormValue = z.infer<
  typeof initiateTransferCustomSchema
>;

export const InitiateTransferCustomForm = ({
  form,
}: {
  form: UseFormReturn<InitiateTransferFormValue>;
}) => {
  const t = useTranslations();

  const value = form.watch();

  return (
    value.transferType === 'CUSTOM_JSON' && (
      <>
        {/* Custom Data Address JSON */}
        <TextareaField
          control={form.control}
          name={'dataAddressJson'}
          placeholder='{"https://w3id.org/edc/v0.0.1/ns/type": "HttpData", ...}'
          label={t('General.customDataSinkJson')}
        />
      </>
    )
  );
};
