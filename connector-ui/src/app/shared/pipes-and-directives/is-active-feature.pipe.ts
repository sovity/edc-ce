/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Pipe, PipeTransform} from '@angular/core';
import {ActiveFeatureSet} from '../../core/config/active-feature-set';
import {EdcUiFeature} from '../../core/config/profiles/edc-ui-feature';

/**
 * Easily check for active features in angular templates.
 */
@Pipe({name: 'isActiveFeature'})
export class IsActiveFeaturePipe implements PipeTransform {
  constructor(private activeFeatureSet: ActiveFeatureSet) {}

  transform(feature: EdcUiFeature): boolean {
    return this.activeFeatureSet.has(feature);
  }
}
