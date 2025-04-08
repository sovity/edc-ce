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
import {type UiAsset} from '@sovity.de/edc-client';
import {useAssetDeleteMutation} from '@/app/(authenticated)/assets/[id]/details/[tab]/components/use-asset-delete-mutation';
import Link from 'next/link';
import {urls} from '@/lib/urls';

const AssetActionMenu = ({asset}: {asset: UiAsset}) => {
  const t = useTranslations();

  const {showDialog, dismissDialog} = useDialogsStore();

  const onDeleteAssetClick = () => {
    const dialogId = `asset-${asset.assetId}-delete`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ActionConfirmDialog
          label={t('Pages.AssetDetails.removeDialogTitle')}
          description={t('Pages.AssetDetails.removeDialogDescription')}
          buttonType="WARNING"
          buttonLabel={t('General.delete')}
          dismiss={() => dismissDialog(dialogId)}
          mutationArgs={asset.assetId}
          useActionMutation={useAssetDeleteMutation}
        />
      ),
    });
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-dropdown-asset-${asset.assetId}`}
          variant="ghost"
          className="h-8 w-8 p-0">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
        <DropdownMenuItem asChild>
          <Link href={urls.assets.detailPage(asset.assetId)}>
            {t('General.viewDetails')}
          </Link>
        </DropdownMenuItem>
        <DropdownMenuItem asChild>
          <Link href={urls.assets.editPage(asset.assetId)}>
            {t('General.edit')}
          </Link>
        </DropdownMenuItem>
        <DropdownMenuItem
          onClick={(e) => {
            onDeleteAssetClick();
            e.stopPropagation();
          }}>
          {t('General.delete')}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default AssetActionMenu;
