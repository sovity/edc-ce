/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

export interface AssetCreateDialogResult {
  /**
   * Updated asset list for the asset page
   */
  refreshedList: UiAssetMapped[];

  /**
   * The updated / created asset
   */
  asset: UiAssetMapped;
}
