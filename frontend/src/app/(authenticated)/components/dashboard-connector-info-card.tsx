/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import CopyTextButton from '@/components/copy-text-button';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {Separator} from '@/components/ui/separator';
import {type BuildInfo, type DashboardPage} from '@/lib/api/client/generated';
import {useTranslations} from 'next-intl';
import React from 'react';
import DashboardConnectorVersion from '@/app/(authenticated)/components/dashboard-connector-version';

export interface DashboardConnectorInfoCardProps {
  data: DashboardPage;
  buildInfo: BuildInfo;
}

export default function DashboardConnectorInfoCard(
  props: DashboardConnectorInfoCardProps,
) {
  const {data, buildInfo} = props;

  const t = useTranslations();

  const connectorEndpointAndParticipantId = `${data.connectorEndpoint}?participantId=${data.connectorParticipantId}`;

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t('Pages.Dashboard.edcConnector')}</CardTitle>
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          {data.connectorTitle}
          <DashboardConnectorVersion buildInfo={buildInfo} variant={'badge'} />
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-4 text-sm">
          <p className="">{data.connectorDescription}</p>
          <Separator />
          <p className="mb-4">{t('Pages.Dashboard.shareDescription')}</p>
        </div>

        <div className="space-y-1">
          <p className="text-xs">
            {t('Pages.Dashboard.connectorEndpointAndParticipantId')}
          </p>
          <CopyTextButton
            dataTestId={'btn-copy-connector-endpoint-and-participant-id'}
            variant={'outline'}
            value={connectorEndpointAndParticipantId}
            label={connectorEndpointAndParticipantId}
          />
        </div>

        <div className="space-y-1">
          <p className="text-xs">{t('Pages.Dashboard.managementApiUrl')}</p>
          <CopyTextButton
            dataTestId={'btn-copy-management-api-url'}
            variant={'outline'}
            value={data.managementApiUrlShownInDashboard}
            label={data.managementApiUrlShownInDashboard}
          />
        </div>
      </CardContent>
    </Card>
  );
}
