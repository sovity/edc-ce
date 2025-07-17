/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ActionConfirmDialog from '@/components/action-confirm-dialog';
import {Button} from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {MoreHorizontal} from 'lucide-react';
import {useTranslations} from 'next-intl';
import type {VaultSecretListPageEntry} from '@sovity.de/edc-client';
import Link from 'next/link';
import {urls} from '@/lib/urls';
import {useVaultSecretDeleteMutation} from './use-vault-secret-delete-mutation';

const VaultSecretActionMenu = ({
  vaultSecret,
}: {
  vaultSecret: VaultSecretListPageEntry;
}) => {
  const t = useTranslations();

  const {showDialog, dismissDialog} = useDialogsStore();

  const onDeleteVaultSecretClick = () => {
    const dialogId = `vault-secret-${vaultSecret.key}-delete`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ActionConfirmDialog
          label={t('Pages.VaultSecretsList.removeDialogTitle')}
          description={t('Pages.VaultSecretsList.removeDialogDescription')}
          buttonType="WARNING"
          buttonLabel={t('General.delete')}
          dismiss={() => dismissDialog(dialogId)}
          mutationArgs={vaultSecret.key}
          useActionMutation={useVaultSecretDeleteMutation}
        />
      ),
    });
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-dropdown-vault-secret-${vaultSecret.key}`}
          variant="ghost"
          className="h-8 w-8 p-0">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
        <DropdownMenuItem asChild>
          <Link href={urls.vaultSecrets.editPage(vaultSecret.key)}>
            {t('General.edit')}
          </Link>
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={(e) => {
            onDeleteVaultSecretClick();
            e.stopPropagation();
          }}>
          {t('General.delete')}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default VaultSecretActionMenu;
