/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ActionConfirmDialog from '@/components/action-confirm-dialog';
import ScrollableDialog from '@/components/scrollable-dialog/scrollable-dialog';
import {Button} from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import type {ContractDefinitionEntry} from '@/lib/api/client/generated';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {MoreHorizontal} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {useDataOfferDeleteMutation} from './use-data-offer-delete-mutation';

const DataOfferActionMenu = ({
  contractDefinitionId,
  dataOffer,
}: {
  contractDefinitionId: string;
  dataOffer: ContractDefinitionEntry;
}) => {
  const t = useTranslations();

  const {showDialog, dismissDialog} = useDialogsStore();

  const onRemoveDataOfferClick = () => {
    const dialogId = `data-offer-${contractDefinitionId}-delete`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ActionConfirmDialog
          label={t('Pages.DataOfferList.removeDialogTitle')}
          description={t('Pages.DataOfferList.removeDialogDescription')}
          buttonType="WARNING"
          buttonLabel={t('General.delete')}
          dismiss={() => dismissDialog(dialogId)}
          mutationArgs={contractDefinitionId}
          useActionMutation={useDataOfferDeleteMutation}
        />
      ),
    });
  };

  const onSeeDataOfferDetailsClick = () => {
    const dialogId = `data-offer-${contractDefinitionId}-details`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ScrollableDialog
          title={t('Pages.DataOfferList.detailsDialogTitle')}
          subtitle={contractDefinitionId}
          content={
            <pre>{JSON.stringify(dataOffer, null, 2)}</pre>
          }></ScrollableDialog>
      ),
    });
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-dropdown-data-offer-${contractDefinitionId}`}
          variant="ghost"
          className="h-8 w-8 p-0">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
        <DropdownMenuItem
          onClick={() => {
            onSeeDataOfferDetailsClick();
          }}>
          {t('General.viewRawJson')}
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => onRemoveDataOfferClick()}>
          {t('General.delete')}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default DataOfferActionMenu;
