/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InternalLink from '@/components/links/internal-link';
import LocalTimeAgo from '@/components/local-time-ago';
import TabsTriggerWithIcon from '@/components/tabs-trigger-with-icon';
import {Alert, AlertDescription, AlertTitle} from '@/components/ui/alert';
import {Tabs, TabsContent, TabsList} from '@/components/ui/tabs';
import {AssetDetailOverviewCard} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/asset-detail-overview-card';
import {AssetDetailPropertiesCard} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/asset-detail-properties-card';
import {canTransfer} from '@/app/(authenticated)/contracts/components/contract-agreement-capabilities';
import {type ContractAgreementCard} from '@/lib/api/client/generated';
import {useTab} from '@/lib/hooks/use-tab';
import {urls} from '@/lib/urls';
import {
  FileClockIcon,
  InfoIcon,
  ReceiptTextIcon,
  TableIcon,
} from 'lucide-react';
import {useTranslations} from 'next-intl';
import ContractTransferTable from './components/contract-transfer-table';
import {ContractDetailCard} from './components/contract-detail-card';
import ContractAgreementHeaderStack from '@/components/stacks/contract-agreement-header-stack';
import {DocumentTextIcon} from '@heroicons/react/24/outline';

export interface ContractDetailsPageContentProps {
  initialTab: string;
  contract: ContractAgreementCard;
}

export default function ContractDetailPage({
  initialTab,
  contract,
}: ContractDetailsPageContentProps) {
  const t = useTranslations();
  const {tab, onTabChange} = useTab(initialTab, (t) =>
    urls.contracts.detailPage(contract.contractAgreementId, t),
  );
  const {asset} = contract;

  return (
    <>
      <ContractAgreementHeaderStack
        size={'page-title'}
        contractAgreementId={contract.contractAgreementId}
        counterpartyParticipantId={contract.counterPartyId}
        terminationStatus={contract.terminationStatus}
        direction={contract.direction}
        assetName={asset.title}>
        {canTransfer(contract) && (
          <InternalLink
            dataTestId={`btn-transfer-quick-access`}
            href={urls.contracts.transferPage(contract.contractAgreementId)}>
            {t('General.transfer')}
          </InternalLink>
        )}
      </ContractAgreementHeaderStack>

      <main className="mt-4 pb-6">
        <Tabs value={tab} onValueChange={onTabChange}>
          <TabsList>
            <TabsTriggerWithIcon
              value="contract-agreement"
              Icon={ReceiptTextIcon}
              label={t('General.contractAgreement')}
            />
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
            <TabsTriggerWithIcon
              value="transfers"
              Icon={FileClockIcon}
              label="Transfer History"
            />
          </TabsList>

          {contract.terminationStatus === 'TERMINATED' && (
            <Alert variant={'destructive'} className={'mt-6'}>
              <InfoIcon className="h-4 w-4" />
              <AlertTitle>{contract.terminationInformation!.reason}</AlertTitle>
              <AlertDescription>
                <div className="mt-2 text-[0.9rem] text-red-700">
                  {contract.terminationInformation!.detail}
                </div>
                <div className="mt-2 text-xs">
                  <LocalTimeAgo
                    date={contract.terminationInformation!.terminatedAt}
                  />
                  &nbsp;·&nbsp;
                  {contract.terminationInformation?.terminatedBy === 'SELF'
                    ? t('General.terminatedByYou')
                    : t('General.terminatedByCounterparty')}
                </div>
              </AlertDescription>
            </Alert>
          )}

          <TabsContent value="contract-agreement" className="mt-6">
            <ContractDetailCard contract={contract} />
          </TabsContent>

          <TabsContent value="overview" className="mt-6">
            <AssetDetailOverviewCard data={asset} />
          </TabsContent>

          <TabsContent value="properties" className="mt-6">
            <AssetDetailPropertiesCard data={asset} />
          </TabsContent>

          <TabsContent value="transfers" className="mt-6">
            <ContractTransferTable data={contract.transferProcesses} />
          </TabsContent>
        </Tabs>
      </main>
    </>
  );
}
