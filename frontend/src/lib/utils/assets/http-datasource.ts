/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type UiAsset} from '@/lib/api/client/generated';

export const buildHttpDatasourceParametrizationHints = (
  asset: UiAsset,
): string | undefined => {
  const res: string[] = [];

  if (asset.httpDatasourceHintsProxyMethod) {
    res.push('Method');
  }

  if (asset.httpDatasourceHintsProxyPath) {
    res.push('Path');
  }

  if (asset.httpDatasourceHintsProxyQueryParams) {
    res.push('Query Params');
  }

  if (asset.httpDatasourceHintsProxyBody) {
    res.push('Body');
  }

  if (res.length === 0) {
    return undefined;
  }

  return res.join(', ');
};
