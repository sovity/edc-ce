/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {env} from '@/env';
import {buildEdcClient} from '@sovity.de/edc-client';
import {EDC_FAKE_BACKEND} from './fake-backend/edc-fake-backend';

export const api = buildEdcClient({
  managementApiUrl: env.NEXT_PUBLIC_MANAGEMENT_API_URL,
  managementApiKey: env.NEXT_PUBLIC_MANAGEMENT_API_KEY,
  configOverrides: {
    fetchApi: env.NEXT_PUBLIC_USE_FAKE_BACKEND ? EDC_FAKE_BACKEND : undefined,
  },
});
