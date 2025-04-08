/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type DashboardPage, type UiConfig} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import {StatCard} from './stat-card';

export interface DashboardStatCardsProps {
  data: DashboardPage;
  config: UiConfig;
}

export const DashboardStatCards = (props: DashboardStatCardsProps) => {
  const {data, config} = props;
  const t = useTranslations();
  const numContractAgreements =
    data.numContractAgreementsConsuming + data.numContractAgreementsProviding;

  return (
    <div className="mb-6 grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-5">
      <StatCard
        title={t('Pages.Dashboard.yourDataOffers')}
        value={data.numContractDefinitions}
      />
      <StatCard
        title={t('Pages.Dashboard.yourAssets')}
        value={data.numAssets}
      />
      <StatCard
        title={t('Pages.Dashboard.yourPolicies')}
        value={data.numPolicies}
      />
      <StatCard
        title={t('Pages.Dashboard.preconfiguredCatalogs')}
        value={config.preconfiguredCounterparties.length}
      />
      <StatCard
        title={t('Pages.Dashboard.contractAgreements')}
        value={numContractAgreements}
      />
    </div>
  );
};
