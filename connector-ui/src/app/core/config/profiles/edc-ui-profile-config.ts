/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {AppConfig} from '../app-config';
import {EdcUiThemeConfig} from './edc-ui-theme-config';

export type EdcUiProfileConfig = Pick<AppConfig, 'features' | 'routes'> &
  EdcUiThemeConfig;
