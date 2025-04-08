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
import PolicyEditor from '@/components/policy-editor/editor/policy-editor';
import {usePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import {sovityDataspacePolicyContext} from '@/components/policy-editor/supported-policies';
import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {usePolicyCreateMutation} from './use-policy-create-mutation';
import {
  type PolicyCreateFormValue,
  usePolicyCreateForm,
} from './use-policy-create-form';
import {useTranslations} from 'next-intl';

export const PolicyCreateForm = () => {
  const mutation = usePolicyCreateMutation();
  const {form} = usePolicyCreateForm();
  const t = useTranslations();

  // Supported Policies
  const policyContext = sovityDataspacePolicyContext();
  const policyEditor = usePolicyEditor(policyContext, form, 'policy');

  async function onSubmit(values: PolicyCreateFormValue) {
    await mutation.mutateAsync({
      policyDefinitionId: values.policyDefinitionId,
      expression: policyEditor.getUiPolicyExpression(),
    });
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <FormGroup
          title={t('Pages.PolicyCreate.generalTitle')}
          subTitle={t('Pages.PolicyCreate.generalSubtitle')}>
          <InputField
            control={form.control}
            name="policyDefinitionId"
            placeholder={t('Pages.PolicyCreate.policyDefinitionIdPlaceholder')}
            label={t('Pages.PolicyCreate.policyDefinitionIdLabel')}
            isRequired
          />
        </FormGroup>
        <FormGroup
          title={t('Pages.PolicyCreate.expressionTitle')}
          subTitle={t('Pages.PolicyCreate.expressionSubtitle')}>
          <PolicyEditor policyEditor={policyEditor} />
        </FormGroup>
        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('Pages.PolicyCreate.submit')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
