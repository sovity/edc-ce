/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InternalLink from '@/components/links/internal-link';
import {ContractAgreementDirection} from '@/lib/api/client/generated';
import {urls} from '@/lib/urls';

interface AssetLinkProps {
  assetId: string;
  assetName: string;
  withId?: boolean;
  contractAgreementDirection?: ContractAgreementDirection;
}

export const AssetLink = ({
  assetId,
  assetName,
  withId,
  contractAgreementDirection = ContractAgreementDirection.Providing,
}: AssetLinkProps) => {
  const isProviding =
    contractAgreementDirection === ContractAgreementDirection.Providing;

  const result = isProviding ? (
    <InternalLink
      dataTestId={`asset-link-${assetId}`}
      href={urls.assets.detailPage(assetId)}
      variant="link"
      size="fit">
      {assetName}
    </InternalLink>
  ) : (
    <div>{assetName}</div>
  );
  return (
    <div className="flex flex-col items-start justify-start">
      {result}
      {withId && <span className="text-xs text-gray-500">{assetId}</span>}
    </div>
  );
};
