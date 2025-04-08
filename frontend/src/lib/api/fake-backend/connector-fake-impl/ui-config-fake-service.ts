/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type UiConfig} from '@sovity.de/edc-client';

export interface UiConfigFakeBackendOption {
  name: string;
  config: UiConfig;
}

export const UI_CONFIG_OPTIONS: UiConfigFakeBackendOption[] = [
  {
    name: 'CE',
    config: {
      features: ['OPEN_SOURCE_MARKETING'],
      preconfiguredCounterparties: [
        {
          connectorEndpoint: 'https://existing-other-connector/api/dsp',
          participantId: 'BPNL1234XX.C1234XX',
        },
        {
          connectorEndpoint:
            'https://does-not-exist-but-is-super-long-so-we-can-test/api/dsp',
          participantId: 'BPNL1234XX.C1235XX',
        },
        {
          connectorEndpoint:
            'https://how-wrapping-works-in-subtext-of-catalog-url-select/api/dsp',
          participantId: 'BPNL1234XX.C1237XX',
        },
      ],
    },
  },
];

let uiConfigInstance = UI_CONFIG_OPTIONS[0]!.config;

export const uiConfig = (): UiConfig => uiConfigInstance;

export const setUiConfig = (config: UiConfig) => {
  uiConfigInstance = config;
};
