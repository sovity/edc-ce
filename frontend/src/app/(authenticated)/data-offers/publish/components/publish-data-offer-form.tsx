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
import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {useDataOfferPublishMutation} from '@/app/(authenticated)/data-offers/publish/components/use-data-offer-publish-mutation';
import {
  type PublishDataOfferFormValue,
  usePublishDataOfferForm,
} from './use-publish-data-offer-form';
import {
  type PolicyDefinitionDto,
  UiCriterionLiteralType,
  UiCriterionOperator,
} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import {ASSET_ID_PROPERTY_NAME} from '@/components/policy-editor/renderer/asset-selector-property-label';
import {AsyncComboboxField} from '@/components/form/async-combobox-field';
import {queryKeys} from '@/lib/queryKeys';
import {api} from '@/lib/api/client';

interface PublishDataOfferFormProps {
  policies: PolicyDefinitionDto[];
}

const PublishDataOfferForm = ({policies}: PublishDataOfferFormProps) => {
  const {form} = usePublishDataOfferForm();
  const t = useTranslations();
  const mutation = useDataOfferPublishMutation();

  async function onSubmit(values: PublishDataOfferFormValue) {
    const assetSelector = [
      {
        operandLeft: ASSET_ID_PROPERTY_NAME,
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
          <AsyncComboboxField
            multiselect
            isRequired
            name={'assetIdList'}
            control={form.control}
            label={t('Pages.PublishDataOffer.assetsTitle')}
            selectPlaceholder={t('General.selectItems')}
            buildQueryKey={(query) =>
              queryKeys.assets.listPage({
                query,
                page: 0,
                pageSize: 0,
                sorting: [],
              })
            }
            loadItems={(query) =>
              api.uiApi
                .assetListPage({
                  assetListPageFilter: {
                    query,
                  },
                })
                .then((data) =>
                  data.content.map((asset) => ({
                    id: asset.assetId,
                    label: asset.title,
                    description: asset.description,
                  })),
                )
            }
            searchPlaceholder={t('General.search')}
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
