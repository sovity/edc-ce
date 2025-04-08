/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ActionConfirmDialog from '@/components/action-confirm-dialog';
import TabsTriggerWithIcon from '@/components/tabs-trigger-with-icon';
import {Button} from '@/components/ui/button';
import {Tabs, TabsContent, TabsList} from '@/components/ui/tabs';
import {useAssetDeleteMutation} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/use-asset-delete-mutation';
import {type UiAsset} from '@/lib/api/client/generated';
import {useTab} from '@/lib/hooks/use-tab';
import {urls} from '@/lib/urls';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {DocumentTextIcon} from '@heroicons/react/24/outline';
import {TableIcon} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {AssetDetailOverviewCard} from './asset-detail-overview-card';
import {AssetDetailPropertiesCard} from './asset-detail-properties-card';
import AssetHeaderStack from '@/components/stacks/asset-header-stack';
import InternalLink from '@/components/links/internal-link';

export interface AssetDetailsPageContentProps {
  initialTab: string;
  asset: UiAsset;
}

export default function AssetDetailPageContent({
  initialTab,
  asset,
}: AssetDetailsPageContentProps) {
  const {tab, onTabChange} = useTab(initialTab, (t) =>
    urls.assets.detailPage(asset.assetId, t),
  );

  const t = useTranslations();

  const {showDialog, dismissDialog} = useDialogsStore();
  const onDeleteClick = () => {
    const dialogId = `asset-${asset.assetId}-delete`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ActionConfirmDialog
          label={t('Pages.AssetDetails.removeDialogTitle')}
          description={t('Pages.AssetDetails.removeDialogDescription')}
          buttonType={'WARNING'}
          buttonLabel={t('General.delete')}
          useActionMutation={useAssetDeleteMutation}
          mutationArgs={asset.assetId}
          dismiss={() => dismissDialog(dialogId)}
        />
      ),
    });
  };

  return (
    <div>
      <AssetHeaderStack
        assetId={asset.assetId}
        assetName={asset.title}
        dataSourceAvailability={asset.dataSourceAvailability}
        size={'page-title'}>
        <div className="flex gap-2">
          <Button
            dataTestId={'btn-delete'}
            size={'default'}
            variant={'destructive'}
            onClick={onDeleteClick}>
            {t('General.delete')}
          </Button>

          <InternalLink
            dataTestId={'btn-edit'}
            size={'default'}
            variant={'default'}
            href={urls.assets.editPage(asset.assetId)}>
            {t('General.edit')}
          </InternalLink>
        </div>
      </AssetHeaderStack>

      <main className="mt-4 pb-6">
        <Tabs value={tab} onValueChange={onTabChange}>
          <TabsList>
            <TabsTriggerWithIcon
              value="overview"
              Icon={DocumentTextIcon}
              label={t('General.overview')}
            />
            <TabsTriggerWithIcon
              value="properties"
              Icon={TableIcon}
              label={t('General.properties')}
            />
          </TabsList>

          <TabsContent value="overview" className="mt-6 space-y-6">
            <AssetDetailOverviewCard data={asset} />
          </TabsContent>

          <TabsContent value="properties" className="mt-6">
            <AssetDetailPropertiesCard data={asset} />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
}
