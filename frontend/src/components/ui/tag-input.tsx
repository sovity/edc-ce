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
import {Input} from '@/components/ui/input';
import {cn} from '@/lib/utils/css-utils';
import {X} from 'lucide-react';

export type TagInputProps = React.InputHTMLAttributes<HTMLInputElement> & {
  tags: string[];
  setTags: (tags: string[]) => void;
  placeholder?: string;
  disabled?: boolean;
};

export const TagInput = ({
  className,
  tags,
  setTags,
  placeholder = 'Add tag...',
  disabled = false,
  ...props
}: TagInputProps) => {
  const [inputValue, setInputValue] = React.useState('');
  const inputRef = React.useRef<HTMLInputElement>(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      addTag();
    } else if (e.key === 'Backspace' && inputValue === '' && tags.length > 0) {
      removeTag(tags.length - 1);
    }
  };

  const addTag = () => {
    const newTag = inputValue.trim();
    if (newTag) {
      setTags([...tags, newTag]);
      setInputValue('');
    }
  };

  const removeTag = (index: number) => {
    const newTags = [...tags];
    newTags.splice(index, 1);
    setTags(newTags);
  };

  const handleContainerClick = () => {
    inputRef.current?.focus();
  };

  return (
    <div
      className={cn(
        'flex min-h-10 w-full flex-wrap items-center gap-1.5 rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-within:ring-1 focus-within:ring-ring',
        className,
      )}
      onClick={handleContainerClick}>
      {tags.map((tag, index) => (
        <Badge key={`${tag}-${index}`} variant="outline" className="px-2 py-1">
          {tag}
          <Button
            dataTestId={`btn-remove-tag-${tag}`}
            type="button"
            variant="ghost"
            size="fit"
            className="ml-1.5"
            onClick={() => removeTag(index)}
            disabled={disabled}>
            <X className="h-3 w-3" />
            <span className="sr-only">Remove {tag}</span>
          </Button>
        </Badge>
      ))}
      <Input
        ref={inputRef}
        type="text"
        value={inputValue}
        onChange={handleInputChange}
        onKeyDown={handleKeyDown}
        onBlurCapture={addTag}
        placeholder={tags.length === 0 ? placeholder : ''}
        disabled={disabled}
        className="h-auto flex-1 border-0 p-0 focus-visible:ring-0 focus-visible:ring-offset-0"
        {...props}
      />
    </div>
  );
};
