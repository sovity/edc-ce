/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useState} from 'react';
import {Button} from '@/components/ui/button';
import {
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {Input} from '@/components/ui/input';
import {Label} from '@/components/ui/label';
import {Textarea} from '@/components/ui/textarea';
import {useTranslations} from 'next-intl';
import SelectableOption from '../dialogs/selectable-option';
import {Checkbox} from '../ui/checkbox';
import {useContractTerminateMutation} from './use-contract-terminate-mutation';

interface TerminateContractAgreementDialogProps {
  contractAgreementId: string;
  dismiss: () => void;
}

export function TerminateContractAgreementDialog({
  contractAgreementId,
  dismiss,
}: TerminateContractAgreementDialogProps) {
  const t = useTranslations();
  const reason = 'Terminated by user';
  const [detailedReason, setDetailedReason] = useState('');
  const [isChecked, setIsChecked] = useState(false);

  const mutation = useContractTerminateMutation(() => dismiss());

  const handleSubmit = async (e: React.FormEvent) => {
    if (!isChecked || !detailedReason.length) {
      return;
    }
    e.preventDefault();

    await mutation.mutateAsync({
      contractAgreementId,
      reason,
      detailedReason,
    });
  };

  return (
    <div>
      <DialogHeader>
        <DialogTitle>{t('Pages.ContractList.terminateContract')}</DialogTitle>
        <DialogDescription>
          {t('Pages.ContractList.terminateText')}
        </DialogDescription>
      </DialogHeader>
      <form onSubmit={handleSubmit}>
        <div className="space-y-4 py-4">
          <div className="space-y-2">
            <Label htmlFor="reason">{t('Pages.ContractList.reason')}</Label>
            <Input
              id="reason"
              disabled
              placeholder={t('Pages.ContractList.reasonPlaceholder')}
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="detailedReason">
              {t('Pages.ContractList.detailedReason')} {'*'}
            </Label>
            <Textarea
              id="detailedReason"
              value={detailedReason}
              onChange={(e) => setDetailedReason(e.target.value)}
              placeholder={t('Pages.ContractList.detailedReasonPlaceholder')}
              rows={4}
            />
          </div>
        </div>
        <SelectableOption
          htmlFor={'confirm-checkbox'}
          label={t('Pages.ContractList.confirmCheckbox')}>
          <Checkbox
            id={'confirm-checkbox'}
            dataTestId={'confirm-checkbox'}
            checked={isChecked}
            onCheckedChange={() => setIsChecked((prev) => !prev)}
          />
        </SelectableOption>
        <DialogFooter>
          <Button
            dataTestId={'btn-cancel'}
            variant="outline"
            type="button"
            onClick={dismiss}>
            {t('General.cancel')}
          </Button>
          <Button
            dataTestId={'btn-terminate'}
            type="submit"
            variant="destructive"
            isLoading={mutation.isLoading}
            disabled={
              mutation.isLoading || !isChecked || !detailedReason.length
            }>
            {t('General.terminate')}
          </Button>
        </DialogFooter>
      </form>
    </div>
  );
}
