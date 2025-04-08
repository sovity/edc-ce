/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InputField from '@/components/form/input-field';
import KeyValuePairsField from '@/components/form/key-value-pairs-field';
import SelectField from '@/components/form/select-field';
import {
  type InitiateTransferFormValue,
  type InitiateTransferType,
} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-schema';
import {UiDataSourceHttpDataMethod} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';

export type InitiateTransferAuthType = 'NONE' | 'VALUE' | 'VAULT_SECRET';

export const initiateTransferHttpSchema = z.object({
  transferType: z.literal('HTTP' satisfies InitiateTransferType),

  httpMethod: z.nativeEnum(UiDataSourceHttpDataMethod),
  httpUrl: z.string().min(1).max(512).url('Please enter a valid URL'),

  httpAdditionalHeaders: z.array(
    z.object({
      key: z.string().min(1).max(128),
      value: z.string().min(1).max(1024),
    }),
  ),

  auth: z.discriminatedUnion('type', [
    z.object({
      type: z.literal('NONE' satisfies InitiateTransferAuthType),
    }),
    z.object({
      type: z.literal('VALUE' satisfies InitiateTransferAuthType),
      headerName: z.string().min(1).max(128),
      headerValue: z.string().min(1).max(1024),
    }),
    z.object({
      type: z.literal('VAULT_SECRET' satisfies InitiateTransferAuthType),
      headerName: z.string().min(1),
      headerSecretName: z.string().max(1024),
    }),
  ]),
});

export type InitiateTransferHttpFormValue = z.infer<
  typeof initiateTransferHttpSchema
>;

export const InitiateTransferHttpForm = ({
  form,
}: {
  form: UseFormReturn<InitiateTransferFormValue>;
}) => {
  const t = useTranslations();

  const value = form.watch();

  return (
    value.transferType === 'HTTP' && (
      <>
        {/* HTTP Data Source: Method */}
        <SelectField
          control={form.control}
          name={'httpMethod'}
          label={t('General.httpMethodLabel')}
          placeholder={t('General.httpMethodPlaceholder')}
          items={Object.values(UiDataSourceHttpDataMethod)
            .filter((it) => it !== 'GET')
            .map((method) => ({
              id: method,
              label: method,
            }))}
        />

        {/* HTTP Data Source: URL */}
        <InputField
          control={form.control}
          name={'httpUrl'}
          placeholder={'https://my-data-source.com/api'}
          label={t('General.httpUrlLabel')}
          isRequired
        />

        {/* HTTP Data Source: Query Params */}
        <KeyValuePairsField
          control={form.control}
          name={'httpQueryParams'}
          label={t('General.httpQueryParamsLabel')}
          keyPlaceholder={t('General.httpQueryParamsKeyPlaceholder')}
          valuePlaceholder={t('General.httpQueryParamsValuePlaceholder')}
        />

        {/* HTTP Data Source: Authentication */}
        <SelectField
          control={form.control}
          name={'auth.type'}
          label={'Authentication'}
          items={[
            {
              id: 'NONE' satisfies InitiateTransferAuthType,
              label: t('General.none'),
            },
            {
              id: 'VALUE' satisfies InitiateTransferAuthType,
              label: t('General.headerWithValue'),
            },
            {
              id: 'VAULT_SECRET' satisfies InitiateTransferAuthType,
              label: t('General.headerWithVaultSecret'),
            },
          ]}
          placeholder={''}
        />

        {/* HTTP Data Source: Auth Header Name */}
        {value.auth.type === 'VALUE' && (
          <div className={'flex gap-4'}>
            <InputField
              className={'grow'}
              control={form.control}
              name={'auth.headerName'}
              placeholder={t('General.httpAuthHeaderNamePlaceholder')}
              label={t('General.httpAuthHeaderNameLabel')}
              isRequired
            />
            <InputField
              className={'grow'}
              control={form.control}
              name={'auth.headerValue'}
              placeholder={t('General.httpAuthHeaderValuePlaceholder')}
              label={t('General.httpAuthHeaderValueLabel')}
              isRequired
            />
          </div>
        )}

        {/* HTTP Data Source: Auth Header Secret Name */}
        {value.auth.type === 'VAULT_SECRET' && (
          <div className={'flex gap-4'}>
            <InputField
              className={'grow'}
              control={form.control}
              name={'auth.headerName'}
              placeholder={t('General.httpAuthHeaderNamePlaceholder')}
              label={t('General.httpAuthHeaderNameLabel')}
              isRequired
            />
            <InputField
              className={'grow'}
              control={form.control}
              name={'auth.headerSecretName'}
              placeholder={'MySecret123'}
              label={t('General.httpAuthHeaderSecretValueLabel')}
              isRequired
            />
          </div>
        )}

        {/* HTTP Data Source: Additional Headers */}
        <KeyValuePairsField
          control={form.control}
          name={'httpAdditionalHeaders'}
          label={t('General.httpAdditionalHeadersLabel')}
          keyPlaceholder={t('General.httpAdditionalHeadersLabel')}
          valuePlaceholder={t('General.httpAdditionalHeaderValuePlaceholder')}
        />
      </>
    )
  );
};
