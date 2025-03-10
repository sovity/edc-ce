/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Inject, Injectable} from '@angular/core';
import {APP_CONFIG, AppConfig} from './app-config';
import {EdcUiFeature} from './profiles/edc-ui-feature';

@Injectable({providedIn: 'root'})
export class ActiveFeatureSet {
  constructor(@Inject(APP_CONFIG) private config: AppConfig) {}

  hasMdsFields(): boolean {
    return this.has('mds-fields');
  }

  hasConnectorLimits(): boolean {
    return this.has('connector-limits');
  }

  has(feature: EdcUiFeature): boolean {
    return this.config.features.has(feature);
  }
}
