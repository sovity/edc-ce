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
import TextareaField from '@/components/form/textarea-field';
import {
  type EditVaultSecretForm as EditVaultSecretFormType,
  useEditVaultSecretForm,
} from './use-edit-vault-secret-form';
import CheckboxField from '@/components/form/checkbox-field';
import {useVaultSecretEditMutation} from './use-vault-secret-edit-mutation';
import type {VaultSecretEditPage} from '@sovity.de/edc-client';

export const EditVaultSecretForm = ({
  id,
  data,
}: {
  id: string;
  data: VaultSecretEditPage;
}) => {
  const {form} = useEditVaultSecretForm(data.description);
  const t = useTranslations();
  const mutation = useVaultSecretEditMutation(id);

  const onSubmit = (data: EditVaultSecretFormType) => {
    mutation.mutate(data);
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <FormGroup
          title={t('Pages.VaultSecretsForm.generalInformationTitle')}
          subTitle={t('Pages.VaultSecretsForm.generalInformationSubtitle')}>
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
          <CheckboxField
            control={form.control}
            label={t('Pages.VaultSecretsForm.editValue')}
            name="editValue"
            isRequired
            item={{
              id: 'editValue',
              label: t('Pages.VaultSecretsForm.editValue'),
            }}
          />
          {form.watch('editValue') && (
            <TextareaField
              type="password"
              control={form.control}
              label={t('Pages.VaultSecretsForm.value')}
              name="value"
              placeholder={
                '-----BEGIN PRIVATE KEY-----\nMIGHAgEAMBMGBy...\n-----END PRIVATE KEY-----'
              }
              isRequired
            />
          )}
        </FormGroup>

        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('General.editVaultSecret')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
