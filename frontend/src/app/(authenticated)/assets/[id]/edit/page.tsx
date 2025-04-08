/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {CenteredLoadingSpinner} from '@/components/loading-spinner';
import {type IdResponseDto} from '@sovity.de/edc-client';
import {useBreadcrumbItems} from '@/lib/breadcrumb-items';
import {useBreadcrumbs} from '@/lib/hooks/use-breadcrumbs';
import {useQueryWrapper} from '@/lib/hooks/use-query-wrapper';
import {useTitle} from '@/lib/hooks/use-title';

import {queryKeys} from '@/lib/queryKeys';
import {throwIfNull} from '@/lib/utils/throw-utils';
import {useTranslations} from 'next-intl';
import PageContainer from '@/components/page-container';
import type {DataOfferCreateFormModel} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {DataOfferForm} from '@/app/(authenticated)/data-offers/create/components/data-offer-form';
import {dataOfferFormValueForEdit} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-value-builder';
import {useAssetEditMutation} from '@/app/(authenticated)/assets/[id]/edit/use-asset-edit-mutation';
import {matchQueryState} from '@/lib/utils/match-query-state';
import {api} from '@/lib/api/client';

export default function AssetEditPage({params}: {params: {id: string}}) {
  const {id} = params;
  const t = useTranslations();
  const breadcrumbItems = useBreadcrumbItems();

  const pageQuery = useQueryWrapper(queryKeys.assets.detailsPage(id), () =>
    api.uiApi
      .getAssetPage()
      .then((data) => data.assets?.find((asset) => asset.assetId === id))
      .then((asset) => throwIfNull(asset, t('Pages.AssetDetails.notFound'))),
  );

  useTitle(pageQuery.data?.title ?? id);
  useBreadcrumbs([
    breadcrumbItems.assets.listPage(),
    breadcrumbItems.assets.detailPage(id, pageQuery.data?.title),
    breadcrumbItems.assets.editPage(id),
  ]);

  const mutation = useAssetEditMutation();
  async function onSubmit(
    formValue: DataOfferCreateFormModel,
  ): Promise<IdResponseDto> {
    return mutation.mutateAsync({formValue, assetId: id});
  }

  return matchQueryState({
    query: pageQuery,
    loading: () => <CenteredLoadingSpinner />,
    success: (data) => {
      return (
        <PageContainer>
          <DataOfferForm
            mode={'EDIT'}
            initialFormValue={dataOfferFormValueForEdit(data)}
            onSubmit={onSubmit}
          />
        </PageContainer>
      );
    },
  });
}
