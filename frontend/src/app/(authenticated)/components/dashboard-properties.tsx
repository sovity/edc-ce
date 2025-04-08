/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import CopyTextButton from '@/components/copy-text-button';
import ExternalLink from '@/components/links/external-link';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {type BuildInfo, type DashboardPage} from '@/lib/api/client/generated';
import {FingerPrintIcon} from '@heroicons/react/24/outline';
import {
  BookTextIcon,
  Building2Icon,
  BuildingIcon,
  HouseIcon,
  IdCardIcon,
  LinkIcon,
  RocketIcon,
  TypeIcon,
} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {AssetProperty} from '../assets/[id]/details/[tab]/components/asset-property';
import DashboardConnectorVersion from '@/app/(authenticated)/components/dashboard-connector-version';
import React from 'react';

export interface DashboardPropertiesProps {
  data: DashboardPage;
  buildInfo: BuildInfo;
}

export const DashboardProperties = (props: DashboardPropertiesProps) => {
  const {data, buildInfo} = props;
  const t = useTranslations();

  return (
    <Card className="mb-6">
      <CardHeader>
        <CardTitle>{t('Pages.Dashboard.connectorProperties')}</CardTitle>
        <CardDescription>{t('General.additionalProperties')}</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 gap-2 md:grid-cols-2 2xl:grid-cols-3">
          <AssetProperty
            Icon={LinkIcon}
            label={t('General.connectorEndpoint')}
            value={
              <CopyTextButton
                dataTestId={'btn-copy-connector-endpoint'}
                value={data.connectorEndpoint}
                label={data.connectorEndpoint}
                variant="outline"
                className="mt-1 px-3 py-2"
              />
            }
          />

          <AssetProperty
            Icon={IdCardIcon}
            label={t('General.participantId')}
            value={
              <CopyTextButton
                dataTestId={'btn-copy-connector-endpoint'}
                value={data.connectorParticipantId}
                label={data.connectorParticipantId}
                variant="outline"
                className="mt-1 px-3 py-2"
              />
            }
          />

          <AssetProperty
            Icon={TypeIcon}
            label={t('General.title')}
            value={data.connectorTitle}
          />

          <AssetProperty
            Icon={RocketIcon}
            label={t('General.connectorVersion')}
            value={
              <div className={'flex gap-2 pt-2'}>
                <DashboardConnectorVersion buildInfo={buildInfo} />
              </div>
            }
          />

          <AssetProperty
            Icon={BuildingIcon}
            label={t('Pages.Dashboard.curatorOrganizationName')}
            value={data.connectorCuratorName}
          />

          <AssetProperty
            Icon={HouseIcon}
            label={t('Pages.Dashboard.curatorOrganizationUrl')}
            value={
              <ExternalLink
                href={data.connectorCuratorUrl}
                noIcon
                showReferrer
                whitespaceNormal>
                {data.connectorCuratorUrl}
              </ExternalLink>
            }
          />

          <AssetProperty
            Icon={BookTextIcon}
            label={t('General.description')}
            value={data.connectorDescription}
          />

          <AssetProperty
            Icon={Building2Icon}
            label={t('Pages.Dashboard.maintainerOrganizationName')}
            value={data.connectorMaintainerName}
          />

          <AssetProperty
            Icon={HouseIcon}
            label={t('Pages.Dashboard.maintainerOrganizationUrl')}
            value={
              <ExternalLink
                href={data.connectorMaintainerUrl}
                noIcon
                showReferrer
                whitespaceNormal>
                {data.connectorMaintainerUrl}
              </ExternalLink>
            }
          />

          {data.connectorDapsConfig?.tokenUrl ? (
            <AssetProperty
              Icon={LinkIcon}
              label={t('Pages.Dashboard.dapsTokenUrl')}
              value={data.connectorDapsConfig?.tokenUrl}
            />
          ) : null}

          {data.connectorDapsConfig?.jwksUrl ? (
            <AssetProperty
              Icon={LinkIcon}
              label={t('Pages.Dashboard.dapsJwksUrl')}
              value={data.connectorDapsConfig?.jwksUrl}
            />
          ) : null}

          {data.connectorCxDidConfig?.myDid ? (
            <AssetProperty
              Icon={FingerPrintIcon}
              label={t('Pages.Dashboard.yourDid')}
              value={
                <CopyTextButton
                  dataTestId={'btn-copy-your-did'}
                  value={data.connectorCxDidConfig?.myDid}
                  label={data.connectorCxDidConfig?.myDid}
                  variant="outline"
                  className="mt-1 px-3 py-2"
                />
              }
            />
          ) : null}

          {data.connectorCxDidConfig?.walletTokenUrl ? (
            <AssetProperty
              Icon={LinkIcon}
              label={t('Pages.Dashboard.walletTokenUrl')}
              value={data.connectorCxDidConfig?.walletTokenUrl}
            />
          ) : null}

          {data.connectorCxDidConfig?.trustedVcIssuer ? (
            <AssetProperty
              Icon={LinkIcon}
              label={t('Pages.Dashboard.trustedVcIssuer')}
              value={data.connectorCxDidConfig?.trustedVcIssuer}
            />
          ) : null}

          {data.connectorCxDidConfig?.bdrsUrl ? (
            <AssetProperty
              Icon={LinkIcon}
              label={t('Pages.Dashboard.bdrsUrl')}
              value={data.connectorCxDidConfig?.bdrsUrl}
            />
          ) : null}

          {data.connectorCxDidConfig?.dimUrl ? (
            <AssetProperty
              Icon={LinkIcon}
              label={t('Pages.Dashboard.dimUrl')}
              value={data.connectorCxDidConfig?.dimUrl}
            />
          ) : null}
        </div>
      </CardContent>
    </Card>
  );
};
