/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {DataOfferForm} from '@/app/(authenticated)/data-offers/create/components/data-offer-form';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useTitle} from '@/lib/hooks/use-title';
import PageContainer from '@/components/page-container';
import {useTranslations} from 'next-intl';
import {dataOfferFormValueForCreate} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-value-builder';
import {type DataOfferCreateFormModel} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {type UiPolicyExpression} from '@sovity.de/edc-client';
import {useDataOfferCreateMutation} from '@/app/(authenticated)/data-offers/create/components/use-data-offer-create-mutation';

export default function DataOfferCreatePage() {
  const t = useTranslations();
  const breadcrumbItems = useBreadcrumbItems();
  useTitle(t('Pages.DataOfferCreate.title'));
  useBreadcrumbs([
    breadcrumbItems.dataOffers.listPage(),
    breadcrumbItems.dataOffers.createPage(),
  ]);

  const mutation = useDataOfferCreateMutation();
  async function onSubmit(
    formValue: DataOfferCreateFormModel,
    expression: UiPolicyExpression,
  ): Promise<void> {
    return mutation.mutate({formValue, expression});
  }

  return (
    <PageContainer>
      <DataOfferForm
        mode={'CREATE'}
        initialFormValue={dataOfferFormValueForCreate()}
        onSubmit={onSubmit}
      />
    </PageContainer>
  );
}
