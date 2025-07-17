/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {
  Control,
  ControllerRenderProps,
  FieldPath,
  FieldValues,
} from 'react-hook-form';
import {useQuery} from '@tanstack/react-query';
import {type ReactNode, useState} from 'react';
import {Command, CommandInput, CommandItem, CommandList} from '../ui/command';
import {Popover, PopoverContent, PopoverTrigger} from '../ui/popover';
import {Button} from '../ui/button';
import {Check, ChevronsUpDown, PlusIcon} from 'lucide-react';
import {useDebounce} from 'use-debounce';
import {cn} from '@/lib/utils/css-utils';
import {FormField, FormItem, FormLabel} from '../ui/form';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {type UiSelectItemOption} from '@/model/ui-select-item-option';

interface AsyncComboboxField<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
> {
  name: TName;
  control: Control<TFieldValues>;
  label: string;
  disabled?: boolean;
  isRequired?: boolean;
  className?: string;
  labelClassName?: string;
  tooltip?: string;
  selectPlaceholder: string;
  searchPlaceholder: string;
  createDescription: string;
  loadItems: (query: string) => Promise<Required<UiSelectItemOption>[]>;
  buildQueryKey: (query: string) => (string | number)[];
  renderCreateDialog: (
    query: string,
    onSubmit: (id: string) => unknown,
  ) => ReactNode;
}

export function AsyncComboboxField<
  TFieldValues extends FieldValues = FieldValues,
  TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>,
>({
  name,
  control,
  label,
  disabled,
  className,
  isRequired,
  selectPlaceholder,
  searchPlaceholder,
  createDescription,
  loadItems,
  buildQueryKey,
  renderCreateDialog,
}: AsyncComboboxField<TFieldValues, TName>) {
  const [open, setOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const [debouncedQuery] = useDebounce(searchQuery, 250);
  const {data, isError, isLoading} = useQuery(
    buildQueryKey(debouncedQuery),
    () => loadItems(debouncedQuery),
  );
  const hasExactMatch = data?.some(
    (item) => item.label === debouncedQuery || item.id === debouncedQuery,
  );

  const {showDialog, dismissDialog} = useDialogsStore();

  const onCreateVaultSecretClick = (field: ControllerRenderProps<any, any>) => {
    const dialogId = 'async-combobox-field-dialog';
    showDialog({
      id: dialogId,
      dialogContent: () =>
        renderCreateDialog(searchQuery, (id) => {
          dismissDialog(dialogId);
          field.onChange(id);
        }),
    });
  };

  return (
    <FormField
      name={name}
      control={control}
      disabled={disabled}
      render={({field}) => (
        <FormItem className={className}>
          <FormLabel className="text-gray-800">
            {label} {isRequired && '*'}
          </FormLabel>
          <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
              <Button
                dataTestId="vault-secret-field"
                variant="outline"
                role="combobox"
                className="justify-between"
                disabled={disabled}>
                {field.value ? field.value : selectPlaceholder}
                <ChevronsUpDown className="opacity-50" />
              </Button>
            </PopoverTrigger>
            <PopoverContent
              side="bottom"
              className="side w-[--radix-popover-trigger-width] p-0">
              <Command shouldFilter={false}>
                <CommandInput
                  value={searchQuery}
                  onValueChange={setSearchQuery}
                  placeholder={searchPlaceholder}
                />
                <CommandList>
                  {!hasExactMatch && searchQuery && (
                    <CommandItem
                      onSelect={() => onCreateVaultSecretClick(field)}>
                      <PlusIcon className="ml-auto" />
                      <div className="flex min-w-0 flex-1 flex-col gap-1">
                        <span className="text-sm font-medium">
                          {searchQuery}
                        </span>
                        <span className="line-clamp-2 break-words text-xs leading-relaxed text-muted-foreground">
                          {createDescription}
                        </span>
                      </div>
                    </CommandItem>
                  )}
                  {isLoading && (
                    <div className="py-6 text-center text-sm">Searching...</div>
                  )}
                  {isError && (
                    <div className="py-6 text-center text-sm">
                      Something went wrong
                    </div>
                  )}
                  {data?.map((item) => (
                    <CommandItem
                      key={item.id}
                      value={item.id}
                      onSelect={() => field.onChange(item.id)}>
                      <Check
                        className={cn(
                          'ml-auto',
                          field.value === item.id ? 'opacity-100' : 'opacity-0',
                        )}
                      />
                      <div className="flex min-w-0 flex-1 flex-col gap-1">
                        <span className="text-sm font-medium">
                          {item.label}
                        </span>
                        <span className="line-clamp-2 break-words text-xs leading-relaxed text-muted-foreground">
                          {item.description}
                        </span>
                      </div>
                    </CommandItem>
                  ))}
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>
        </FormItem>
      )}
    />
  );
}
