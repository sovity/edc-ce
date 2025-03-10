/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {ConnectorLimits} from '@sovity.de/edc-client';
import {ActiveFeatureSet} from '../config/active-feature-set';
import {EdcApiService} from './api/edc-api.service';

@Injectable({
  providedIn: 'root',
})
export class ConnectorLimitsService {
  constructor(
    private edcApiService: EdcApiService,
    private activeFeatureSet: ActiveFeatureSet,
  ) {}

  isConsumingAgreementLimitExceeded(): Observable<boolean> {
    return this.activeFeatureSet.hasConnectorLimits()
      ? this.edcApiService
          .getEnterpriseEditionConnectorLimits()
          .pipe(map(this.limitsExceeded))
      : of(false);
  }

  private limitsExceeded = (limits: ConnectorLimits) => {
    const max = limits.maxActiveConsumingContractAgreements;
    const current = limits.numActiveConsumingContractAgreements;

    return max != null && max >= 0 && current >= max;
  };
}
