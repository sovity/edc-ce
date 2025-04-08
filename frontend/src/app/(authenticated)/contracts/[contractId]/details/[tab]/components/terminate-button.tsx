/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {TerminateContractAgreementDialog} from '@/components/terminate-contract-agreement-dialog/terminate-contract-agreement-dialog';
import {Button} from '@/components/ui/button';
import {canTerminate} from '@/app/(authenticated)/contracts/components/contract-agreement-capabilities';
import {type ContractAgreementCard} from '@/lib/api/client/generated';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {useTranslations} from 'next-intl';

export interface TerminateButtonProps {
  contract: ContractAgreementCard;
}

export default function TerminateButton({contract}: TerminateButtonProps) {
  const t = useTranslations();
  const {showDialog, dismissDialog} = useDialogsStore();

  const onTerminateClick = () => {
    const dialogId = `contract-${contract.contractAgreementId}-terminate`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <TerminateContractAgreementDialog
          contractAgreementId={contract.contractAgreementId}
          dismiss={() => dismissDialog(dialogId)}
        />
      ),
    });
  };

  return (
    <Button
      dataTestId={`btn-terminate`}
      variant={'destructive'}
      disabled={!canTerminate(contract)}
      onClick={onTerminateClick}>
      {t('General.terminate')}
    </Button>
  );
}
