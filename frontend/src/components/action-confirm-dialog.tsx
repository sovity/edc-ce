/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import React, {useState} from 'react';
import {Button} from '@/components/ui/button';
import {
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {type UseMutationResult} from '@tanstack/react-query';
import SelectableOption from './dialogs/selectable-option';
import {Checkbox} from './ui/checkbox';

interface ActionConfirmDialogProps<T, S> {
  label: string;
  description: string;
  buttonType: 'WARNING' | 'DEFAULT';
  buttonLabel: string;
  confirmCheckboxLabel?: string;
  useActionMutation: (dismiss: () => void) => UseMutationResult<S, Error, T>;
  mutationArgs: T;
  children?: React.ReactNode;
  dismiss: () => void;
}

const ActionConfirmDialog = <T, S>({
  useActionMutation,
  children,
  ...props
}: ActionConfirmDialogProps<T, S>) => {
  const [isChecked, setIsChecked] = useState(false);
  const mutation = useActionMutation(props.dismiss);

  const onConfirm = () => {
    mutation.mutate(props.mutationArgs);
  };

  const hasConfirmCheckbox = props.confirmCheckboxLabel !== undefined;

  return (
    <div>
      <DialogHeader>
        <DialogTitle>{props.label}</DialogTitle>
        <DialogDescription>{props.description}</DialogDescription>
      </DialogHeader>
      <div className="mb-5 mt-5 px-3">{children ? children : null}</div>
      {props.confirmCheckboxLabel && (
        <SelectableOption
          htmlFor={'confirm-checkbox'}
          label={props.confirmCheckboxLabel}>
          <Checkbox
            id={'confirm-checkbox'}
            dataTestId={'confirm-checkbox'}
            checked={isChecked}
            onCheckedChange={() => setIsChecked((prev) => !prev)}
          />
        </SelectableOption>
      )}
      <DialogFooter>
        <Button
          type="submit"
          dataTestId={`btn-confirm`}
          onClick={onConfirm}
          disabled={mutation.isLoading || (hasConfirmCheckbox && !isChecked)}
          isLoading={mutation.isLoading}
          variant={props.buttonType === 'WARNING' ? 'destructive' : 'default'}>
          {props.buttonLabel}
        </Button>
      </DialogFooter>
    </div>
  );
};

export default ActionConfirmDialog;
