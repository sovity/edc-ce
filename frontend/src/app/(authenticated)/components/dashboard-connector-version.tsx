/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type BuildInfo} from '@/lib/api/client/generated';
import {env} from '@/env';
import React from 'react';
import DashboardConnectorVersionBadge from '@/app/(authenticated)/components/dashboard-connector-version-badge';

export interface DashboardConnectorVersionProps {
  buildInfo: BuildInfo;
  variant?: 'badge';
}

export default function DashboardConnectorVersion(
  props: DashboardConnectorVersionProps,
) {
  const {buildInfo, variant} = props;

  const backendVersion = buildInfo.buildVersion;
  const backendDate = buildInfo.buildDate;

  const frontendVersion = env.NEXT_PUBLIC_BUILD_VERSION;
  const frontendDate = env.NEXT_PUBLIC_BUILD_DATE;

  return backendVersion === frontendVersion ? (
    <DashboardConnectorVersionBadge
      version={backendVersion}
      date={backendDate}
      variant={variant}
    />
  ) : (
    <>
      <DashboardConnectorVersionBadge
        version={backendVersion}
        date={backendDate}
        variant={variant}
      />
      <DashboardConnectorVersionBadge
        version={frontendVersion}
        date={frontendDate}
        variant={variant}
      />
    </>
  );
}
