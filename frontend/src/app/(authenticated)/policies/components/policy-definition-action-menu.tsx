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
import {type PolicyDefinitionMapped} from '@/app/(authenticated)/policies/components/policy-definition-mapped';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {MoreHorizontal} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {usePolicyDeleteMutation} from './use-policy-delete-mutation';

const PolicyDefinitionActionMenu = ({
  policyDefinition,
}: {
  policyDefinition: PolicyDefinitionMapped;
}) => {
  const t = useTranslations();

  const {showDialog, dismissDialog} = useDialogsStore();

  const onRemovePolicyDefinitionClick = () => {
    const dialogId = `policy-definition-${policyDefinition.policyDefinitionId}-delete`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ActionConfirmDialog
          label={t('Pages.PolicyList.removeDialogTitle')}
          description={t('Pages.PolicyList.removeDialogDescription')}
          buttonType="WARNING"
          buttonLabel={t('General.delete')}
          dismiss={() => dismissDialog(dialogId)}
          mutationArgs={policyDefinition.policyDefinitionId}
          useActionMutation={usePolicyDeleteMutation}
        />
      ),
    });
  };

  const onSeeJsonLd = () => {
    const dialogId = `policy-${policyDefinition.policyDefinitionId}-jsonld`;
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <ScrollableDialog
          title={t('Pages.PolicyList.jsonldDialogTitle')}
          subtitle={policyDefinition.policyDefinitionId}
          content={
            <pre className={'whitespace-pre-wrap'}>
              {JSON.stringify(JSON.parse(policyDefinition.jsonLd), null, 2)}
            </pre>
          }></ScrollableDialog>
      ),
    });
  };

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-dropdown-policy-${policyDefinition.policyDefinitionId}`}
          variant="ghost"
          className="h-8 w-8 p-0">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
        <DropdownMenuItem
          onClick={() => {
            onSeeJsonLd();
          }}>
          {t('General.viewJsonLd')}
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => onRemovePolicyDefinitionClick()}>
          {t('General.delete')}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default PolicyDefinitionActionMenu;
