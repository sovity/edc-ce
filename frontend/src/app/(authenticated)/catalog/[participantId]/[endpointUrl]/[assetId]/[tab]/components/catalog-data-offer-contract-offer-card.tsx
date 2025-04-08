/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {PolicyRenderer} from '@/components/policy-editor/renderer/policy-renderer';
import {sovityDataspacePolicyContext} from '@/components/policy-editor/supported-policies';
import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';
import {type UiAsset, type UiContractOffer} from '@/lib/api/client/generated';
import {useTranslations} from 'next-intl';
import {NegotiateButton} from './negotiate-button';
import PolicyJsonLdButton from '@/app/(authenticated)/contracts/[contractId]/details/[tab]/components/policy-jsonld-button';

export interface AssetDetailsContractsProps {
  asset: UiAsset;
  contractOffers: UiContractOffer[];
  endpointUrl: string;
  participantId: string;
}

export const CatalogDataOfferContractOfferCard = (
  props: AssetDetailsContractsProps,
) => {
  const {contractOffers, asset, endpointUrl, participantId} = props;

  const policyContext = sovityDataspacePolicyContext();

  const t = useTranslations();

  return (
    <div className="space-y-4">
      {contractOffers.map((contractOffer, iContractOffer) => {
        const expression = contractOffer.policy.expression
          ? policyContext.policyExpressionMapper.buildPolicyExpressionMapped(
              contractOffer.policy.expression,
            )
          : null;

        return (
          <Card key={iContractOffer}>
            <CardHeader className="py-5">
              <CardTitle className="text-lg font-medium">
                {t('General.contractOffer')}{' '}
                {contractOffers.length > 1 ? iContractOffer + 1 : ''}
              </CardTitle>
            </CardHeader>
            <CardContent className="py-5">
              {expression ? (
                <div className="flex items-start justify-between">
                  <PolicyRenderer
                    errors={contractOffer.policy.errors}
                    expression={expression}
                  />
                </div>
              ) : null}
              <div className="mt-10 flex justify-end gap-2">
                <PolicyJsonLdButton
                  dialogId={'policy-jsonld'}
                  policy={contractOffer.policy}
                />
                <NegotiateButton
                  assetId={asset.assetId}
                  contractOfferId={contractOffer.contractOfferId}
                  participantId={participantId}
                  endpointUrl={endpointUrl}
                  policyJsonLd={contractOffer.policy.policyJsonLd}
                  isOwnConnector={asset.isOwnConnector}
                />
              </div>
            </CardContent>
          </Card>
        );
      })}
    </div>
  );
};
