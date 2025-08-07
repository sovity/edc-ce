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
import {useBusinessPartnerGroupEditMutation} from './use-business-partner-group-edit-mutation';
import {
  type EditBusinessPartnerGroupForm as EditBusinessPartnerGroupFormType,
  useEditBusinessPartnerGroupForm,
} from './use-edit-business-partner-group-form';
import {BusinessPartnerGroupFormFields} from '../../../components/business-partner-group-form-fields';
import type {BusinessPartnerGroupEditPage} from '@sovity.de/edc-client';

export const EditBusinessPartnerGroupForm = ({
  data,
}: {
  data: BusinessPartnerGroupEditPage;
}) => {
  const {form} = useEditBusinessPartnerGroupForm(data.businessPartnerNumbers);
  const t = useTranslations();
  const mutation = useBusinessPartnerGroupEditMutation(data.groupId);

  const onSubmit = (data: EditBusinessPartnerGroupFormType) => {
    mutation.mutate(data);
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <BusinessPartnerGroupFormFields
          control={form.control}
          groupId={data.groupId}
        />

        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('Pages.BusinessPartnerGroupsEditForm.submit')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
