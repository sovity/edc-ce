/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import FormGroup from '@/components/form/form-group';
import InputField from '@/components/form/input-field';
import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {useTranslations} from 'next-intl';
import {
  useCreateVaultSecretForm,
  type CreateVaultSecretForm as CreateVaultSecretFormType,
} from './use-create-vault-secret-form';
import {useVaultSecretCreateMutation} from './use-vault-secret-create-mutation';
import TextareaField from '@/components/form/textarea-field';

export const CreateVaultSecretForm = ({
  onSubmitExec,
  defaultKey,
  fromDialog = false,
}: {
  showValue?: boolean;
  fromDialog?: boolean;
  onSubmitExec?: (data: CreateVaultSecretFormType) => unknown;
  defaultKey?: string;
}) => {
  const {form} = useCreateVaultSecretForm(defaultKey);
  const t = useTranslations();
  const mutation = useVaultSecretCreateMutation(!fromDialog);

  const onSubmit = (data: CreateVaultSecretFormType) => {
    mutation.mutate(data, {onSuccess: (_, vars) => onSubmitExec?.(vars)});
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <FormGroup
          title={t('Pages.VaultSecretsForm.generalInformationTitle')}
          subTitle={t('Pages.VaultSecretsForm.generalInformationSubtitle')}>
          <InputField
            control={form.control}
            label={t('Pages.VaultSecretsForm.key')}
            placeholder={'myVaultSecret123'}
            name="key"
            isRequired
          />
          <TextareaField
            control={form.control}
            label={t('Pages.VaultSecretsForm.description')}
            name="description"
            placeholder={t('Pages.VaultSecretsForm.descriptionPlaceholder')}
            isRequired
          />
        </FormGroup>
        <FormGroup
          title={t('Pages.VaultSecretsForm.secretValueTitle')}
          subTitle={t('Pages.VaultSecretsForm.secretValueSubtitle')}>
          <TextareaField
            type="password"
            control={form.control}
            label={t('Pages.VaultSecretsForm.value')}
            placeholder={
              '-----BEGIN PRIVATE KEY-----\nMIGHAgEAMBMGBy...\n-----END PRIVATE KEY-----'
            }
            name="value"
            isRequired
          />
        </FormGroup>

        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('Pages.VaultSecretsCreate.submit')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
