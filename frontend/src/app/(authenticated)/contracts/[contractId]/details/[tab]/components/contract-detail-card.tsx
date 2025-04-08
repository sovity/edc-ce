/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import CopyTextButton from '@/components/copy-text-button';
import {PolicyRenderer} from '@/components/policy-editor/renderer/policy-renderer';
import {sovityDataspacePolicyContext} from '@/components/policy-editor/supported-policies';
import {Card, CardContent} from '@/components/ui/card';
import {AssetProperty} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/asset-property';
import {canTerminate} from '@/app/(authenticated)/contracts/components/contract-agreement-capabilities';
import {type ContractAgreementCard} from '@/lib/api/client/generated';
import {getLocalDateTimeString} from '@/lib/utils/dates';
import {FingerPrintIcon} from '@heroicons/react/24/outline';
import {ArrowDownUpIcon, CalendarIcon, LinkIcon, UserIcon} from 'lucide-react';
import {useTranslations} from 'next-intl';
import TerminateButton from '@/app/(authenticated)/contracts/[contractId]/details/[tab]/components/terminate-button';
import PolicyJsonLdButton from '@/app/(authenticated)/contracts/[contractId]/details/[tab]/components/policy-jsonld-button';

export interface ContractAgreementDetailsProps {
  contract: ContractAgreementCard;
}

export const ContractDetailCard = (props: ContractAgreementDetailsProps) => {
  const t = useTranslations();
  const {contract} = props;

  const policyContext = sovityDataspacePolicyContext();

  const expression = contract.contractPolicy.expression
    ? policyContext.policyExpressionMapper.buildPolicyExpressionMapped(
        contract.contractPolicy.expression,
      )
    : null;

  return (
    <div className="space-y-4">
      {expression ? (
        <Card>
          <CardContent className="py-5">
            <div className="grid grid-cols-1 gap-2 md:grid-cols-2 2xl:grid-cols-3">
              <AssetProperty
                Icon={FingerPrintIcon}
                label={t('General.contractAgreementId')}
                value={contract.contractAgreementId}
              />

              <AssetProperty
                Icon={CalendarIcon}
                label={t('General.signedAt')}
                value={getLocalDateTimeString(contract.contractSigningDate)}
              />

              <AssetProperty
                Icon={ArrowDownUpIcon}
                label={t('General.direction')}
                value={contract.direction}
              />

              <AssetProperty
                Icon={UserIcon}
                label={t('General.counterpartyId')}
                value={contract.counterPartyId}
              />

              <AssetProperty
                Icon={LinkIcon}
                label={t('General.counterpartyConnectorEndpoint')}
                value={
                  <CopyTextButton
                    dataTestId={'btn-copy-counter-party-endpoint'}
                    value={contract.counterPartyAddress}
                    label={contract.counterPartyAddress}
                    variant="outline"
                    className="mt-1 px-3 py-2"
                  />
                }
              />
            </div>
            <div className="mt-8">
              <div className="mb-3 font-medium">
                {t('Pages.PublishDataOffer.contractPolicy')}
              </div>
              {expression ? (
                <div className="flex items-start justify-between">
                  <PolicyRenderer
                    errors={contract.contractPolicy.errors}
                    expression={expression}
                  />
                </div>
              ) : null}
              <div className="mt-10 flex justify-between gap-2">
                <div>
                  {canTerminate(contract) && (
                    <TerminateButton contract={contract} />
                  )}
                </div>
                <PolicyJsonLdButton
                  dialogId={'policy-jsonld'}
                  policy={contract.contractPolicy}
                />
              </div>
            </div>
          </CardContent>
        </Card>
      ) : null}
    </div>
  );
};
