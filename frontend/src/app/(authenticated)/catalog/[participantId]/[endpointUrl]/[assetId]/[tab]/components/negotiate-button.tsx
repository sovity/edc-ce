/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect, useState} from 'react';
import ActionConfirmDialog from '@/components/action-confirm-dialog';
import {Button} from '@/components/ui/button';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {useToast} from '@/components/ui/use-toast';
import {
  type ContractNegotiationRequest,
  type UiContractNegotiation,
} from '@/lib/api/client/generated';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {CheckIcon} from '@heroicons/react/24/outline';
import {HandshakeIcon} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {useDataOfferNegotiateMutation} from './use-data-offer-negotiate-mutation';
import {useContractNegotiationStatus} from './use-contract-negotiation-status';

export interface NegotiateButtonProps {
  assetId: string;
  contractOfferId: string;
  participantId: string;
  endpointUrl: string;
  policyJsonLd: string;
  isOwnConnector: boolean;
}

export const NegotiateButton = (props: NegotiateButtonProps) => {
  const {
    assetId,
    contractOfferId,
    participantId,
    endpointUrl,
    policyJsonLd,
    isOwnConnector,
  } = props;

  const [negotiationId, setNegotiationId] = useState<string | null>(null);
  const {showDialog, dismissDialog} = useDialogsStore();
  const t = useTranslations();
  const {toast} = useToast();

  const statusQuery = useContractNegotiationStatus(negotiationId);
  const isNegotiationSuccess = statusQuery.data?.contractAgreementId != null;

  const onNegotiateClick = () => {
    const dialogId = `data-offer-negotiate`;
    const request: ContractNegotiationRequest = {
      assetId,
      contractOfferId,
      counterPartyId: participantId,
      counterPartyAddress: endpointUrl,
      policyJsonLd,
    };

    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ActionConfirmDialog
          label={t('Pages.CatalogDataOfferDetails.negotiateConfirmTitle')}
          description={t(
            'Pages.CatalogDataOfferDetails.negotiateConfirmDescription',
          )}
          buttonType="DEFAULT"
          buttonLabel={t('General.confirm')}
          confirmCheckboxLabel={t(
            'Pages.CatalogDataOfferDetails.negotiateCheckboxLabel',
          )}
          dismiss={() => dismissDialog(dialogId)}
          mutationArgs={request}
          useActionMutation={(dismiss) =>
            // eslint-disable-next-line react-hooks/rules-of-hooks
            useDataOfferNegotiateMutation((data: UiContractNegotiation) => {
              dismiss();
              setNegotiationId(data.contractNegotiationId);
            })
          }
        />
      ),
    });
  };

  useEffect(() => {
    if (statusQuery.isSuccess && isNegotiationSuccess) {
      toast({
        title: t('Pages.CatalogDataOfferDetails.negotiatedSuccess'),
        description: `${t('Pages.CatalogDataOfferDetails.negotiatedSuccessDescription')}`,
      });
    }
  }, [statusQuery.isSuccess, isNegotiationSuccess, toast, t]);

  return (
    <>
      {isOwnConnector ? (
        <TooltipProvider>
          <Tooltip>
            <TooltipTrigger asChild>
              <div>
                <Button
                  dataTestId={'btn-negotiate'}
                  disabled
                  className="flex items-center gap-1.5">
                  <HandshakeIcon className="h-4 w-4" />
                  {t('General.negotiate')}
                </Button>
              </div>
            </TooltipTrigger>
            <TooltipContent>
              {t('Pages.CatalogDataOfferDetails.cantNegotiateWithOwnConnector')}
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      ) : (
        <Button
          dataTestId={'btn-negotiate'}
          className="flex items-center gap-1.5"
          isLoading={negotiationId != null && !isNegotiationSuccess}
          loadingText={t('Pages.CatalogDataOfferDetails.negotiating')}
          disabled={negotiationId != null}
          onClick={() => onNegotiateClick()}>
          {isNegotiationSuccess ? (
            <CheckIcon className="h-4 w-4" />
          ) : (
            <HandshakeIcon className="h-4 w-4" />
          )}
          {isNegotiationSuccess
            ? t('Pages.CatalogDataOfferDetails.negotiatedSuccess')
            : t('General.negotiate')}
        </Button>
      )}
    </>
  );
};
