/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useRef, useState} from 'react';
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
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {Popover, PopoverContent, PopoverTrigger} from '@/components/ui/popover';
import {useWidthObserver} from '@/lib/hooks/use-width-observer';
import {cn} from '@/lib/utils/css-utils';
import {type UiSelectItemGroup} from '@/model/ui-select-item-group';
import {Check, ChevronsUpDown} from 'lucide-react';
import {type Control} from 'react-hook-form';

export interface ComboboxFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  itemGroups: UiSelectItemGroup[];
  selectPlaceholder: string;
  searchPlaceholder: string;
  searchEmptyMessage: string;
  onChangeExec?: (value: string) => void;
  isRequired?: boolean;
}

const ComboboxField = ({
  control,
  name,
  label,
  selectPlaceholder,
  searchPlaceholder,
  searchEmptyMessage,
  itemGroups,
  onChangeExec,
  isRequired,
}: ComboboxFieldProps) => {
  const selectButtonRef = useRef<HTMLButtonElement>(null);
  const fieldWidth = useWidthObserver(selectButtonRef, 300);
  const [isOpen, setIsOpen] = useState(false);

  const items = itemGroups.flatMap((group) => group.items);
  const getItem = (id: string) => items.find((item) => item.id === id);

  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => (
        <FormItem>
          <FormLabel className="text-gray-800">
            {label} {isRequired && '*'}
          </FormLabel>
          <Popover open={isOpen} onOpenChange={setIsOpen}>
            <PopoverTrigger asChild>
              <FormControl>
                <Button
                  ref={selectButtonRef}
                  dataTestId={`form-combobox-${name}-trigger`}
                  variant="outline"
                  role="combobox"
                  className={cn(
                    'w-full justify-between',
                    !field.value && 'text-muted-foreground hover:text-gray-500',
                  )}>
                  {field.value
                    ? getItem(field.value as string)?.label
                    : selectPlaceholder}
                  <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                </Button>
              </FormControl>
            </PopoverTrigger>
            <PopoverContent className="p-0" style={{width: `${fieldWidth}px`}}>
              <Command>
                <CommandInput
                  data-testid={`form-combobox-${name}-search`}
                  placeholder={searchPlaceholder}
                />
                <CommandList>
                  {itemGroups.map((group, i) => (
                    <CommandGroup
                      key={`command-group-${group.heading}-${i}`}
                      heading={group.heading}>
                      {group.items.map((x) => (
                        <CommandItem
                          data-testid={`form-combobox-${name}-item-${x.id}`}
                          key={x.id}
                          keywords={[x.label]}
                          value={x.id}
                          onSelect={() => {
                            field.onChange(x.id);
                            setIsOpen(false);

                            if (onChangeExec) {
                              onChangeExec(x.id);
                            }
                          }}>
                          <Check
                            className={cn(
                              'mr-2 h-4 w-4',
                              field.value === x.id ? 'visible' : 'invisible',
                            )}
                          />
                          {x.label}
                        </CommandItem>
                      ))}
                    </CommandGroup>
                  ))}
                  <CommandEmpty>{searchEmptyMessage}</CommandEmpty>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export default ComboboxField;
