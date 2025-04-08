/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import * as React from 'react';
import {Badge} from '@/components/ui/badge';
import {Button} from '@/components/ui/button';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {Popover, PopoverContent, PopoverTrigger} from '@/components/ui/popover';
import {cn} from '@/lib/utils/css-utils';
import {Check, ChevronsUpDown, X} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {type Control, type ControllerRenderProps} from 'react-hook-form';

export type Option = {
  id: string;
  label: string;
};

interface MultiSelectComboboxFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  options: Option[];
  selectPlaceholder: string;
  onChange?: (value: string[]) => void;
  value?: string[];
  disabled?: boolean;
  className?: string;
  isRequired?: boolean;
}

const MultiSelectComboboxField = ({
  control,
  name,
  label,
  options,
  selectPlaceholder,
  onChange,
  value = [],
  disabled = false,
  className,
  isRequired,
}: MultiSelectComboboxFieldProps) => {
  const [open, setOpen] = React.useState(false);
  const [selected, setSelected] = React.useState<string[]>(value);
  const t = useTranslations();

  const handleSelect = React.useCallback(
    (itemId: string, field: ControllerRenderProps<any, any>) => {
      const updatedSelection = selected.includes(itemId)
        ? selected.filter((id) => id !== itemId)
        : [...selected, itemId];

      setSelected(updatedSelection);
      if (onChange) {
        onChange(updatedSelection);
      }

      field.onChange(updatedSelection);
    },
    [selected, onChange],
  );

  const handleRemove = React.useCallback(
    (itemId: string, field: ControllerRenderProps<any, any>) => {
      const updatedSelection = selected.filter((id) => id !== itemId);
      setSelected(updatedSelection);
      if (onChange) {
        onChange(updatedSelection);
      }

      field.onChange(updatedSelection);
    },
    [selected, onChange],
  );

  const selectedItems = React.useMemo(() => {
    return options.filter((option) => selected.includes(option.id));
  }, [options, selected]);

  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => (
        <FormItem>
          <FormLabel className="text-gray-800">
            {label} {isRequired && '*'}
          </FormLabel>
          <div className={cn('space-y-2', className)}>
            <Popover open={open} onOpenChange={setOpen}>
              <PopoverTrigger asChild>
                <Button
                  dataTestId={`form-combobox-${name}-trigger`}
                  variant="outline"
                  role="combobox"
                  aria-expanded={open}
                  className={cn(
                    'w-full justify-between',
                    !selected.length && 'text-muted-foreground',
                  )}
                  disabled={disabled}>
                  {selected.length > 0
                    ? `${selected.length} selected`
                    : selectPlaceholder}
                  <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                </Button>
              </PopoverTrigger>
              <PopoverContent
                className="p-0"
                style={{width: 'var(--radix-popover-trigger-width)'}}
                sideOffset={4}
                alignOffset={0}
                avoidCollisions={true}>
                <Command>
                  <CommandInput placeholder={t('General.searchItems')} />
                  <CommandList>
                    <CommandEmpty>{t('General.noItemsFound')}</CommandEmpty>
                    <CommandGroup>
                      {options.map((option) => {
                        const isSelected = selected.includes(option.id);
                        return (
                          <CommandItem
                            key={option.id}
                            value={option.id}
                            onSelect={() => handleSelect(option.id, field)}>
                            <span className="flex-1">{option.label}</span>
                            {isSelected && (
                              <Check className="ml-2 h-4 w-4 text-primary" />
                            )}
                          </CommandItem>
                        );
                      })}
                    </CommandGroup>
                  </CommandList>
                </Command>
              </PopoverContent>
            </Popover>

            {selectedItems.length > 0 && (
              <div className="mt-2 flex flex-wrap gap-1">
                {selectedItems.map((item) => (
                  <Badge
                    key={item.id}
                    variant="secondary"
                    className={cn(
                      'inline-flex items-center justify-center rounded-md',
                      'px-2 py-1 text-xs font-medium',
                      'ring-1 ring-inset',
                      'whitespace-nowrap',
                      'bg-gray-50 text-gray-700 ring-gray-600/20',
                    )}>
                    {item.label}
                    <button
                      type="button"
                      className="ml-1 rounded-full outline-none focus:ring-2 focus:ring-offset-1"
                      onClick={() => handleRemove(item.id, field)}
                      aria-label={`Remove ${item.label}`}>
                      <X className="h-3 w-3" />
                    </button>
                  </Badge>
                ))}
              </div>
            )}
          </div>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default MultiSelectComboboxField;
