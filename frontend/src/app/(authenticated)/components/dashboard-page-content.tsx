/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  type BuildInfo,
  type DashboardPage,
  type UiConfig,
} from '@/lib/api/client/generated';
import {DashboardAbout} from './dashboard-about';
import {DashboardCharts} from './dashboard-charts';
import {DashboardProperties} from './dashboard-properties';
import {DashboardStatCards} from './dashboard-stat-cards';

export interface DashboardPageContentProps {
  data: DashboardPage;
  config: UiConfig;
  buildInfo: BuildInfo;
}

export default function DashboardPageContent(props: DashboardPageContentProps) {
  const {data, config, buildInfo} = props;

  const showMarketingCard = config.features.includes('OPEN_SOURCE_MARKETING');

  return (
    <main>
      <DashboardCharts data={data} buildInfo={buildInfo} />

      <DashboardStatCards data={data} config={config} />

      <DashboardProperties data={data} buildInfo={buildInfo} />

      <DashboardAbout showMarketingCard={showMarketingCard} />
    </main>
  );
}
