/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import Link from 'next/link';
import {TerminateContractAgreementDialog} from '@/components/terminate-contract-agreement-dialog/terminate-contract-agreement-dialog';
import {Button} from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  canTerminate,
  canTransfer,
} from '@/app/(authenticated)/contracts/components/contract-agreement-capabilities';
import {urls} from '@/lib/urls';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {type ContractAgreementCard} from '@sovity.de/edc-client';
import {MoreHorizontal} from 'lucide-react';
import {useTranslations} from 'next-intl';

interface ContractActionsMenuProps {
  contractAgreement: ContractAgreementCard;
}

const ContractActionMenu = ({contractAgreement}: ContractActionsMenuProps) => {
  const t = useTranslations();
  const {showDialog, dismissDialog} = useDialogsStore();

  const onTerminateClick = () => {
    const dialogId = `contract-${contractAgreement.contractAgreementId}-terminate`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <TerminateContractAgreementDialog
          contractAgreementId={contractAgreement.contractAgreementId}
          dismiss={() => dismissDialog(dialogId)}
        />
      ),
    });
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-dropdown-contract-${contractAgreement.contractAgreementId}`}
          variant="ghost"
          className="h-8 w-8 p-0">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
        {
          <DropdownMenuItem asChild>
            <Link
              href={urls.contracts.detailPage(
                contractAgreement.contractAgreementId,
              )}>
              {t('General.viewDetails')}
            </Link>
          </DropdownMenuItem>
        }
        {canTransfer(contractAgreement) && (
          <DropdownMenuItem asChild>
            <Link
              href={urls.contracts.transferPage(
                contractAgreement.contractAgreementId,
              )}>
              {t('Pages.InitiateTransfer.title')}
            </Link>
          </DropdownMenuItem>
        )}
        {canTerminate(contractAgreement) && (
          <>
            <DropdownMenuItem onClick={onTerminateClick}>
              {t('General.terminate')}
            </DropdownMenuItem>
          </>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default ContractActionMenu;
