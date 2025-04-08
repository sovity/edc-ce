/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect, useState} from 'react';
import {CheckIcon, CopyIcon} from 'lucide-react';
import {copyToClipboard} from './copy-text-button';
import {Button} from './ui/button';
import {Label} from './ui/label';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from './ui/tooltip';

const CopyTextField = ({
  label,
  value,
  dataTestId,
}: {
  label?: string;
  value: string;
  dataTestId: string;
}) => {
  const [hasCopied, setHasCopied] = useState(false);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setHasCopied(false);
    }, 2000);

    return () => {
      clearTimeout(timeout);
    };
  }, [hasCopied]);

  return (
    <div>
      {label && <Label className="mb-1 text-gray-800">{label}</Label>}
      <div className="flex items-center rounded-md">
        <div
          className={
            'flex h-10 w-full rounded-md rounded-r-none border border-input bg-background px-3 py-2 text-sm text-gray-600 ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground'
          }>
          {value}
        </div>
        <TooltipProvider>
          <Tooltip open={hasCopied}>
            <TooltipTrigger asChild>
              <Button
                dataTestId={dataTestId}
                variant={'outline'}
                size="fit"
                className="h-10 w-16 rounded-l-none border-l-0"
                onClick={async () => {
                  await copyToClipboard(value);
                  setHasCopied(true);
                }}>
                <span className="sr-only">Copy</span>
                {hasCopied ? (
                  <CheckIcon className="h-4 w-4 text-green-600" />
                ) : (
                  <CopyIcon className="h-4 w-4 rotate-90" />
                )}
              </Button>
            </TooltipTrigger>
            <TooltipContent>Copied!</TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>
    </div>
  );
};

export default CopyTextField;
