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
import {type DataSourceHttpAuthType} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-http-form';
import VaultSecretField from '@/components/vault-secret-field';

export type InitiateTransferAuthType = 'NONE' | 'VAULT_SECRET';

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
      type: z.literal('VAULT_SECRET' satisfies InitiateTransferAuthType),
      headerName: z.string().min(1),
      headerSecretName: z.string().max(1024),
    }),
    z.object({
      type: z.literal('BASIC' satisfies DataSourceHttpAuthType),
      username: z.string().min(1).max(1024),
      password: z.string().min(1).max(1024),
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
              id: 'VAULT_SECRET' satisfies InitiateTransferAuthType,
              label: t('General.authCustomHeader'),
            },
            {
              id: 'BASIC' satisfies DataSourceHttpAuthType,
              label: t('General.authBasic'),
            },
          ]}
          placeholder={''}
        />

        {/* HTTP Data Source: Auth Header via Secret */}
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
            <VaultSecretField
              className={'grow'}
              control={form.control}
              name={'auth.headerSecretName'}
              label={t('General.httpAuthHeaderSecretValueLabel')}
              isRequired
            />
          </div>
        )}

        {/* HTTP Data Source: Auth Basic */}
        {value.auth.type === 'BASIC' && (
          <>
            <InputField
              control={form.control}
              name={'auth.username'}
              placeholder={'my-username'}
              label={t('General.username')}
              isRequired
            />
            <InputField
              control={form.control}
              name={'auth.password'}
              placeholder={'my-password'}
              label={t('General.password')}
              isRequired
            />
            <div className={'text-sm'}>
              {t('General.basicAuthUsageWarning')}
            </div>
          </>
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
