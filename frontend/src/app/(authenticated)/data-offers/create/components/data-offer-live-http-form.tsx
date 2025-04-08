/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import CheckboxField from '@/components/form/checkbox-field';
import InputField from '@/components/form/input-field';
import KeyValuePairsField from '@/components/form/key-value-pairs-field';
import SelectField from '@/components/form/select-field';
import {type DataOfferLiveType} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {UiDataSourceHttpDataMethod} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {z} from 'zod';

export type DataSourceHttpAuthType = 'NONE' | 'VALUE' | 'VAULT_SECRET';

export const dataOfferLiveHttpSchema = z.object({
  offerLiveType: z.literal('HTTP' satisfies DataOfferLiveType),

  httpMethod: z.nativeEnum(UiDataSourceHttpDataMethod),
  httpUrl: z.string().url('Please enter a valid URL').min(1).max(1024),
  httpQueryParams: z.array(
    z.object({
      key: z.string().min(1).max(128),
      value: z.string().max(128).optional(),
    }),
  ),
  httpAdditionalHeaders: z.array(
    z.object({
      key: z.string().min(1).max(128),
      value: z.string().min(1).max(1024),
    }),
  ),

  auth: z.discriminatedUnion('type', [
    z.object({
      type: z.literal('NONE' satisfies DataSourceHttpAuthType),
    }),
    z.object({
      type: z.literal('VALUE' satisfies DataSourceHttpAuthType),
      headerName: z.string().min(1).max(128),
      headerValue: z.string().min(1).max(1024),
    }),
    z.object({
      type: z.literal('VAULT_SECRET' satisfies DataSourceHttpAuthType),
      headerName: z.string().min(1).max(128),
      headerSecretName: z.string().min(1).max(1024),
    }),
  ]),

  httpMethodParameterization: z.boolean(),
  httpPathParameterization: z.boolean(),
  httpQueryParamsParameterization: z.boolean(),
  httpRequestBodyParameterization: z.boolean(),
});

export type DataOfferLiveHttpFormValue = z.infer<
  typeof dataOfferLiveHttpSchema
>;

export const DataOfferLiveHttpForm = ({
  form,
  formKeyDataOfferTypeLive,
}: {
  form: UseFormReturn<any>;
  formKeyDataOfferTypeLive: string;
}) => {
  const t = useTranslations();

  const fieldKey = (key: string): string =>
    formKeyDataOfferTypeLive === ''
      ? formKeyDataOfferTypeLive
      : `${formKeyDataOfferTypeLive}.${key}`;

  const value = form.watch(
    formKeyDataOfferTypeLive,
  ) as DataOfferLiveHttpFormValue;

  return (
    value.offerLiveType === 'HTTP' && (
      <>
        {/* HTTP Data Source: Method */}
        {!value.httpMethodParameterization && (
          <SelectField
            control={form.control}
            name={fieldKey('httpMethod')}
            label={t('Pages.DataOfferCreate.method')}
            placeholder={t('Pages.DataOfferCreate.method')}
            items={Object.values(UiDataSourceHttpDataMethod).map((method) => ({
              id: method,
              label: method,
            }))}
          />
        )}

        {/* HTTP Data Source: URL */}
        <InputField
          control={form.control}
          name={fieldKey('httpUrl')}
          placeholder={'https://my-data-source.com/api'}
          label={
            value.httpPathParameterization
              ? t('General.baseUrl')
              : t('General.url')
          }
          isRequired
        />

        {/* HTTP Data Source: Query Params */}
        <KeyValuePairsField
          control={form.control}
          name={fieldKey('httpQueryParams')}
          label={
            value.httpQueryParamsParameterization
              ? t('General.defaultQueryParams')
              : t('General.queryParams')
          }
          keyPlaceholder={t('General.queryParamName')}
          valuePlaceholder={t('General.queryParamValue')}
        />

        {/* HTTP Data Source: Authentication */}
        <SelectField
          control={form.control}
          name={fieldKey('auth.type')}
          label={t('General.authentication')}
          items={[
            {
              id: 'NONE' satisfies DataSourceHttpAuthType,
              label: t('General.none'),
            },
            {
              id: 'VALUE' satisfies DataSourceHttpAuthType,
              label: t('General.headerWithValue'),
            },
            {
              id: 'VAULT_SECRET' satisfies DataSourceHttpAuthType,
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
              name={fieldKey('auth.headerName')}
              placeholder={t('General.authorization')}
              label={t('General.authorizationHeaderName')}
              isRequired
            />
            <InputField
              className={'grow'}
              control={form.control}
              name={fieldKey('auth.headerValue')}
              placeholder={t('General.basic') + '...'}
              label={t('General.authorizationHeaderValue')}
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
              name={fieldKey('auth.headerName')}
              placeholder={t('General.authorization')}
              label={t('General.authorizationHeaderName')}
              isRequired
            />
            <InputField
              className={'grow'}
              control={form.control}
              name={fieldKey('auth.headerSecretName')}
              placeholder={'MySecret123'}
              label={t('General.vaultSecretName')}
              isRequired
            />
          </div>
        )}

        {/* HTTP Data Source: Method Parameterization */}
        <CheckboxField
          control={form.control}
          name={fieldKey('httpMethodParameterization')}
          label={t('General.parameterization') + ':' + t('General.method')}
          item={{
            id: 'httpMethodParameterization',
            label: t('General.enableMethodParameterization'),
            description: t('General.enableMethodParameterizationDescription'),
          }}
        />

        {/* HTTP Data Source: Path Parameterization */}
        <CheckboxField
          control={form.control}
          name={fieldKey('httpPathParameterization')}
          label={t('General.parameterization') + ':' + t('General.path')}
          item={{
            id: 'httpPathParameterization',
            label: t('General.enablePathParameterization'),
            description: t('General.enablePathParameterizationDescription'),
          }}
        />

        {/* HTTP Data Source: QueryParam Parameterization */}
        <CheckboxField
          control={form.control}
          name={fieldKey('httpQueryParamsParameterization')}
          label={t('General.parameterization') + ':' + t('General.queryParams')}
          item={{
            id: 'httpQueryParamsParameterization',
            label: t('General.enableQueryParamParameterization'),
            description: t(
              'General.enableQueryParamParameterizationDescription',
            ),
          }}
        />

        {/* HTTP Data Source: Body Parameterization */}
        <CheckboxField
          control={form.control}
          name={fieldKey('httpBodyParameterization')}
          label={t('General.parameterization') + ':' + t('General.body')}
          item={{
            id: 'httpQueryParamParameterization',
            label: t('General.enableBodyParameterization'),
            description: t('General.enableBodyParameterizationDescription'),
          }}
        />

        {/* HTTP Data Source: Additional Headers */}
        <KeyValuePairsField
          control={form.control}
          name={fieldKey('httpAdditionalHeaders')}
          label={t('General.additionalHeaders')}
          keyPlaceholder={t('General.headerName')}
          valuePlaceholder={t('General.headerValue')}
        />
      </>
    )
  );
};
