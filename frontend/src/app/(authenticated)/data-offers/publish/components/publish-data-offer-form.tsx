/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ComboboxField from '@/components/form/combobox-field';
import FormGroup from '@/components/form/form-group';
import InputField from '@/components/form/input-field';
import MultiSelectComboboxField from '@/components/form/multi-select-combobox-field';
import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {useDataOfferPublishMutation} from '@/app/(authenticated)/data-offers/publish/components/use-data-offer-publish-mutation';
import {
  type PublishDataOfferFormValue,
  usePublishDataOfferForm,
} from './use-publish-data-offer-form';
import {
  type PolicyDefinitionDto,
  type UiAsset,
  UiCriterionLiteralType,
  UiCriterionOperator,
} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';

interface PublishDataOfferFormProps {
  assets: UiAsset[];
  policies: PolicyDefinitionDto[];
}

const PublishDataOfferForm = ({
  assets,
  policies,
}: PublishDataOfferFormProps) => {
  const {form} = usePublishDataOfferForm();
  const t = useTranslations();
  const mutation = useDataOfferPublishMutation();

  async function onSubmit(values: PublishDataOfferFormValue) {
    const assetSelector = [
      {
        operandLeft: 'https://w3id.org/edc/v0.0.1/ns/id',
        operator: UiCriterionOperator.In,
        operandRight: {
          type: UiCriterionLiteralType.ValueList,
          valueList: values.assetIdList,
        },
      },
    ];

    await mutation.mutateAsync({
      contractDefinitionRequest: {
        contractDefinitionId: values.dataOfferId,
        contractPolicyId: values.contractPolicy,
        accessPolicyId: values.accessPolicy,
        assetSelector: assetSelector,
      },
    });
  }

  const assetOptions = assets.map((asset) => ({
    id: asset.assetId,
    label: asset.title,
  }));

  const policyOptions = [
    {
      heading: t('Pages.PublishDataOffer.policiesTitle'),
      items: policies.map((policy) => ({
        id: policy.policyDefinitionId,
        label: policy.policyDefinitionId,
      })),
    },
  ];

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <FormGroup
          title={t('Pages.PublishDataOffer.dataOfferTitle')}
          subTitle={t('Pages.PublishDataOffer.dataOfferDescription')}>
          <InputField
            isRequired
            control={form.control}
            name={'dataOfferId'}
            label={t('Pages.PublishDataOffer.dataOfferId')}
            placeholder={'my-data-offer'}
          />
        </FormGroup>
        <FormGroup
          title={t('Pages.PublishDataOffer.assetsTitle')}
          subTitle={t('Pages.PublishDataOffer.assetsDescription')}>
          <MultiSelectComboboxField
            isRequired
            control={form.control}
            name={'assetIdList'}
            label={t('Pages.PublishDataOffer.assetsTitle')}
            selectPlaceholder={t('General.selectItems')}
            options={assetOptions}
          />
        </FormGroup>
        <FormGroup
          title={t('Pages.PublishDataOffer.policiesTitle')}
          subTitle={t('Pages.PublishDataOffer.policiesDescription')}>
          <ComboboxField
            isRequired
            control={form.control}
            name={'accessPolicy'}
            label={t('Pages.PublishDataOffer.accessPolicy')}
            itemGroups={policyOptions}
            selectPlaceholder={t('General.selectPolicy')}
            searchPlaceholder={t('General.search')}
            searchEmptyMessage={'No policies found'}
          />
          <ComboboxField
            isRequired
            control={form.control}
            name={'contractPolicy'}
            label={t('Pages.PublishDataOffer.contractPolicy')}
            itemGroups={policyOptions}
            selectPlaceholder={t('General.selectPolicy')}
            searchPlaceholder={t('General.search')}
            searchEmptyMessage={'No policies found'}
          />
        </FormGroup>
        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('Pages.PublishDataOffer.createDataOffer')}
          </Button>
        </div>
      </form>
    </Form>
  );
};

export default PublishDataOfferForm;
