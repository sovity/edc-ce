/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {useTranslations} from 'next-intl';
import {useBusinessPartnerGroupCreateMutation} from './use-business-partner-group-create-mutation';
import {
  type CreateBusinessPartnerGroupForm as CreateBusinessPartnerGroupFormType,
  useCreateBusinessPartnerGroupForm,
} from './use-create-business-partner-group-form';
import {BusinessPartnerGroupFormFields} from '../../components/business-partner-group-form-fields';

export const CreateBusinessPartnerGroupForm = () => {
  const {form} = useCreateBusinessPartnerGroupForm();
  const t = useTranslations();
  const mutation = useBusinessPartnerGroupCreateMutation();

  const onSubmit = (data: CreateBusinessPartnerGroupFormType) => {
    mutation.mutate(data);
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <BusinessPartnerGroupFormFields control={form.control} />

        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('Pages.BusinessPartnerGroupsCreateForm.submit')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
