/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InternalLink from '@/components/links/internal-link';
import TabsTriggerWithIcon from '@/components/tabs-trigger-with-icon';
import {Tabs, TabsContent, TabsList} from '@/components/ui/tabs';
import {AssetDetailOverviewCard} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/asset-detail-overview-card';
import {AssetDetailPropertiesCard} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/asset-detail-properties-card';
import {type UiDataOffer} from '@/lib/api/client/generated';
import {useTab} from '@/lib/hooks/use-tab';
import {urls} from '@/lib/urls';
import {DocumentTextIcon} from '@heroicons/react/24/outline';
import {ReceiptTextIcon, TableIcon} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {CatalogDataOfferContractOfferCard} from './components/catalog-data-offer-contract-offer-card';
import CatalogDataOfferHeaderStack from '@/components/stacks/catalog-data-offer-header-stack';

export interface CatalogDataOfferDetailPageProps {
  initialTab: string;
  data: UiDataOffer;
}

export default function CatalogDataOfferDetailPage({
  initialTab,
  data,
}: CatalogDataOfferDetailPageProps) {
  const {tab, onTabChange} = useTab(initialTab, (t) =>
    urls.catalog.detailPage(
      data.participantId,
      data.endpoint,
      data.asset.assetId,
      t,
    ),
  );

  const t = useTranslations();

  const {asset, contractOffers, endpoint, participantId} = data;

  const isOnRequestAsset = asset.dataSourceAvailability === 'ON_REQUEST';

  return (
    <div>
      <CatalogDataOfferHeaderStack
        assetId={asset.assetId}
        participantId={participantId}
        endpointUrl={endpoint}
        assetName={asset.title}
        dataSourceAvailability={asset.dataSourceAvailability}
        size={'page-title'}>
        {isOnRequestAsset && tab !== 'properties' && (
          <InternalLink
            dataTestId={'btn-contact-quick-access'}
            href={urls.catalog.detailPage(
              participantId,
              endpoint,
              asset.assetId,
              'properties',
              'contact',
            )}
            variant="default">
            {t('General.contact')}
          </InternalLink>
        )}

        {!isOnRequestAsset && tab !== 'contract-offers' && (
          <InternalLink
            dataTestId={'btn-negotiate-quick-access'}
            href={urls.catalog.detailPage(
              participantId,
              endpoint,
              asset.assetId,
              'contract-offers',
            )}
            variant="default">
            {t('General.negotiate')}
          </InternalLink>
        )}
      </CatalogDataOfferHeaderStack>

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
            {!isOnRequestAsset && (
              <TabsTriggerWithIcon
                value="contract-offers"
                Icon={ReceiptTextIcon}
                label={t('General.contractOffers')}
              />
            )}
          </TabsList>

          <TabsContent value="overview" className="mt-6 space-y-6">
            <AssetDetailOverviewCard data={asset} />
          </TabsContent>

          <TabsContent value="properties" className="mt-6">
            <AssetDetailPropertiesCard data={asset} />
          </TabsContent>

          {!isOnRequestAsset && (
            <TabsContent value="contract-offers" className="mt-6">
              <CatalogDataOfferContractOfferCard
                contractOffers={contractOffers}
                asset={asset}
                endpointUrl={endpoint}
                participantId={participantId}
              />
            </TabsContent>
          )}
        </Tabs>
      </main>
    </div>
  );
}
