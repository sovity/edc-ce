/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type UiAsset} from '@/lib/api/client/generated';
import {getLocalDateString} from '../dates';

export const getTemporalCoverageStr = (asset: UiAsset): string | undefined => {
  if (
    asset.temporalCoverageFrom === undefined &&
    asset.temporalCoverageToInclusive === undefined
  ) {
    return undefined;
  }

  if (
    asset.temporalCoverageFrom !== undefined &&
    asset.temporalCoverageToInclusive !== undefined
  ) {
    return `${getLocalDateString(asset.temporalCoverageFrom)} - ${getLocalDateString(asset.temporalCoverageToInclusive)}`;
  }

  if (asset.temporalCoverageFrom !== undefined) {
    return `From ${getLocalDateString(asset.temporalCoverageFrom)}`;
  }

  if (asset.temporalCoverageToInclusive !== undefined) {
    return `Until ${getLocalDateString(asset.temporalCoverageToInclusive)}`;
  }
};
