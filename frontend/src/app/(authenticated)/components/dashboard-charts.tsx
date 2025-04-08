/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  type BuildInfo,
  type DashboardPage,
  type DashboardTransferAmounts,
} from '@/lib/api/client/generated';
import {ArcElement, Chart as ChartJS, Legend, Tooltip} from 'chart.js';
import {useTranslations} from 'next-intl';
import {Doughnut} from 'react-chartjs-2';
import DashboardConnectorInfoCard from '@/app/(authenticated)/components/dashboard-connector-info-card';
import {type TranslateFn} from '@/lib/utils/translation-utils';

ChartJS.register(ArcElement, Tooltip, Legend);

export interface DashboardChartsProps {
  data: DashboardPage;
  buildInfo: BuildInfo;
}

export const DashboardCharts = (props: DashboardChartsProps) => {
  const {data, buildInfo} = props;

  const t = useTranslations();

  const incomingData = buildChart(data.transferProcessesConsuming, t);
  const outgoingData = buildChart(data.transferProcessesProviding, t);

  return (
    <div className="mb-6 grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
      <Card>
        <CardHeader className="pb-2">
          <CardTitle>{t('Pages.Dashboard.incomingData')}</CardTitle>
          <CardDescription>
            {t('Pages.Dashboard.transferProcesses')}
          </CardDescription>
        </CardHeader>
        {data.transferProcessesConsuming.numTotal === 0 ? (
          <CardContent className="flex h-64 items-center justify-center">
            <p className="text-sm text-muted-foreground">
              {t('Pages.Dashboard.noData')}
            </p>
          </CardContent>
        ) : (
          <>
            <CardContent className="h-64">
              <Doughnut data={incomingData} options={chartOptions} />
            </CardContent>
            <CardFooter className="justify-center">
              <p className="text-sm text-muted-foreground">
                {t('General.total')}: {data.transferProcessesConsuming.numTotal}
              </p>
            </CardFooter>
          </>
        )}
      </Card>

      <Card>
        <CardHeader className="pb-2">
          <CardTitle>{t('Pages.Dashboard.outgoingData')}</CardTitle>
          <CardDescription>
            {t('Pages.Dashboard.transferProcesses')}
          </CardDescription>
        </CardHeader>
        {data.transferProcessesProviding.numTotal === 0 ? (
          <CardContent className="flex h-64 items-center justify-center">
            <p className="text-sm text-muted-foreground">
              {t('Pages.Dashboard.noData')}
            </p>
          </CardContent>
        ) : (
          <>
            <CardContent className="h-64">
              <Doughnut data={outgoingData} options={chartOptions} />
            </CardContent>
            <CardFooter className="justify-center">
              <p className="text-sm text-muted-foreground">
                {t('General.total')}: {data.transferProcessesProviding.numTotal}
              </p>
            </CardFooter>
          </>
        )}
      </Card>

      <DashboardConnectorInfoCard data={data} buildInfo={buildInfo} />
    </div>
  );
};

const buildTransferChartData = (data: DashboardTransferAmounts) => {
  return [data.numOk, data.numRunning, data.numError];
};

const buildChart = (data: DashboardTransferAmounts, t: TranslateFn) => {
  return {
    labels: [
      t('Pages.Dashboard.completed'),
      t('Pages.Dashboard.inProgress'),
      t('Pages.Dashboard.error'),
    ],
    datasets: [
      {
        data: buildTransferChartData(data),
        backgroundColor: ['#10b981', '#60a5fa', '#ef4444'],
        borderColor: ['#10b981', '#60a5fa', '#ef4444'],
        borderWidth: 1,
        cutout: '65%',
      },
    ],
  };
};

const chartOptions = {
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        usePointStyle: true,
        padding: 20,
      },
    },
  },
  maintainAspectRatio: false,
};
