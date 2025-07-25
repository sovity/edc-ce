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
import {Check, ChevronsUpDown} from 'lucide-react';
import {type Control} from 'react-hook-form';

export interface ComboboxFieldProps {
  control: Control<any, any>;
  name: string;
  label: string;
  selectPlaceholder: string;
  searchPlaceholder: string;
  searchEmptyMessage: string;
  addValueLabel: string;
  isRequired?: boolean;
}

const ArrayComboboxField = ({
  control,
  name,
  label,
  selectPlaceholder,
  searchPlaceholder,
  searchEmptyMessage,
  addValueLabel,
  isRequired,
}: ComboboxFieldProps) => {
  const selectButtonRef = useRef<HTMLButtonElement>(null);
  const fieldWidth = useWidthObserver(selectButtonRef, 300);
  const [isOpen, setIsOpen] = useState(false);
  const [query, setQuery] = useState('');
  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => {
        const value = field.value as string[];
        const filteredValue = value.filter((i) => i.includes(query));
        return (
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
                      'w-full justify-between overflow-hidden',
                      !value.length &&
                        'text-muted-foreground hover:text-gray-500',
                    )}>
                    <div className="truncate">
                      {value.length > 0 ? value.join(', ') : selectPlaceholder}
                    </div>
                    <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                  </Button>
                </FormControl>
              </PopoverTrigger>
              <PopoverContent
                className="p-0"
                style={{width: `${fieldWidth}px`}}>
                <Command shouldFilter={false}>
                  <CommandInput
                    value={query}
                    onValueChange={setQuery}
                    placeholder={searchPlaceholder}
                  />
                  <CommandList>
                    {query.length > 0 && !value.includes(query) && (
                      <CommandGroup heading="New Entry">
                        <CommandItem
                          onSelect={() => {
                            field.onChange([
                              ...(field.value as string[]),
                              query,
                            ]);
                            setQuery('');
                          }}>
                          {addValueLabel} {query}
                        </CommandItem>
                      </CommandGroup>
                    )}
                    {filteredValue.length > 0 && (
                      <CommandGroup heading="Existing Entries">
                        {filteredValue.map((item) => (
                          <CommandItem
                            key={item}
                            value={item}
                            onSelect={() =>
                              field.onChange(value.filter((i) => i !== item))
                            }>
                            <Check className={cn('mr-2 h-4 w-4')} />
                            {item}
                          </CommandItem>
                        ))}
                      </CommandGroup>
                    )}
                    <CommandEmpty>{searchEmptyMessage}</CommandEmpty>
                  </CommandList>
                </Command>
              </PopoverContent>
            </Popover>
            <FormMessage />
          </FormItem>
        );
      }}
    />
  );
};

export default ArrayComboboxField;
