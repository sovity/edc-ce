/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  type ChangeEventHandler,
  type DragEventHandler,
  useRef,
  useState,
} from 'react';
import {Button} from '@/components/ui/button';
import {Card, CardContent} from '@/components/ui/card';
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {Textarea} from '@/components/ui/textarea';
import {cn} from '@/lib/utils/css-utils';
import FormLabelContent from './form-label-content';
import {type TextFieldProps} from './input-field';

function typeIsHtmlNode(target: EventTarget): target is Node {
  return 'nodeType' in target;
}

const TextareaFileDropField = ({
  isRequired,
  control,
  name,
  label,
  tooltip,
  placeholder,
}: TextFieldProps) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [isDraggedOver, setIsDraggedOver] = useState(false);

  return (
    <FormField
      control={control}
      name={name}
      render={({field}) => {
        const fileReader = new FileReader();
        fileReader.onload = (evt) => {
          const value = evt.target?.result;
          if (typeof value === 'string') {
            field.onChange(value);
          }
        };

        const handleDragOver: DragEventHandler<HTMLDivElement> = (
          dragOverEvent,
        ) => {
          dragOverEvent.preventDefault();
        };

        const handleDragLeave: DragEventHandler<HTMLDivElement> = (evt) => {
          // prevent setting draggedOver to false when in some child-element
          if (
            !evt.relatedTarget ||
            !typeIsHtmlNode(evt.relatedTarget) ||
            !evt.currentTarget.contains(evt.relatedTarget)
          ) {
            setIsDraggedOver(false);
          }
        };

        const handleDrop: DragEventHandler<HTMLDivElement> = (dragEvent) => {
          dragEvent.preventDefault();
          setIsDraggedOver(false);

          const items = dragEvent.dataTransfer?.items ?? [];
          if (items.length > 0) {
            const file = items[0]?.getAsFile();
            if (file) {
              fileReader.readAsText(file);
            }
          }
        };

        const handleFileUpload: ChangeEventHandler<HTMLInputElement> = (
          clickEvent,
        ) => {
          const files = clickEvent.target.files ?? [];
          if (files.length > 0) {
            const file = files[0];
            if (file) {
              fileReader.readAsText(file);
            }
          }
        };

        return (
          <FormItem>
            <FormLabel>
              <FormLabelContent
                label={label}
                tooltip={tooltip}
                isRequired={isRequired}
              />
            </FormLabel>
            <FormControl>
              <>
                <input
                  ref={fileInputRef}
                  type="file"
                  hidden
                  onChange={handleFileUpload}
                />
                <Card className="h-24 whitespace-nowrap border-dashed border-gray-400">
                  <CardContent
                    onDragOver={handleDragOver}
                    onDragEnter={() => setIsDraggedOver(true)}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                    className={cn(
                      'flex h-full items-center justify-center pb-0',
                      isDraggedOver && 'bg-gray-100',
                    )}>
                    <span>
                      <Button
                        dataTestId={`form-btn-file-drop-${name}-upload`}
                        type="button"
                        className="px-0 text-base"
                        variant="link"
                        onClick={(evt) => {
                          evt.preventDefault();
                          fileInputRef.current?.click();
                        }}>
                        Upload
                      </Button>{' '}
                      or drop a file here
                    </span>
                  </CardContent>
                </Card>
                <Textarea
                  data-testid={`form-textarea-file-drop-${name}-textarea`}
                  className="mt-2 h-56"
                  placeholder={placeholder}
                  {...field}
                />
              </>
            </FormControl>
            <FormMessage />
          </FormItem>
        );
      }}
    />
  );
};

export default TextareaFileDropField;
