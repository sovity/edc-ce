/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {ASSET_ID_PROPERTY_NAME} from '@/components/policy-editor/renderer/asset-selector-property-label';
import {AssetLink} from '@/components/links/asset-link';

export const AssetSelectorValue = ({
  value,
  assetProperty,
}: {
  value: string | null | undefined;
  assetProperty: string;
}) => {
  const isAssetId = assetProperty === ASSET_ID_PROPERTY_NAME && value != null;
  return (
    <span className="break-all">
      {isAssetId ? (
        <AssetLink assetId={value} assetName={value} />
      ) : (
        (value ?? 'null')
      )}
    </span>
  );
};
