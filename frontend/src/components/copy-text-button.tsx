/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect, useState} from 'react';
import {cn} from '@/lib/utils/css-utils';
import {EyeIcon, EyeSlashIcon} from '@heroicons/react/24/outline';
import {CheckIcon, CopyIcon} from 'lucide-react';
import {Button, type ButtonProps} from './ui/button';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from './ui/tooltip';

export async function copyToClipboard(value: string) {
  await navigator.clipboard.writeText(value);
}

interface CopyTextButtonProps extends ButtonProps {
  value: string;
  label: string;
  dataTestId: string;
  confidential?: boolean;
}

const CopyTextButton = ({
  value,
  label,
  className,
  dataTestId,
  variant = 'ghost',
  confidential,
  ...props
}: CopyTextButtonProps) => {
  const [hasCopied, setHasCopied] = useState(false);
  const [isVisible, setIsVisible] = useState(!confidential);

  useEffect(() => {
    if (hasCopied) {
      const timeout = setTimeout(() => setHasCopied(false), 2000);
      return () => clearTimeout(timeout);
    }
  }, [hasCopied]);

  return (
    <TooltipProvider>
      <Tooltip open={hasCopied}>
        <TooltipTrigger asChild>
          <Button
            dataTestId={dataTestId}
            size="fit"
            variant={variant}
            className={cn(
              'flex w-full items-center justify-between gap-2 px-4 py-3',
              '!whitespace-normal !break-all !text-left',
              className,
            )}
            onClick={async () => {
              await copyToClipboard(value);
              setHasCopied(true);
            }}
            {...props}>
            <div className="overflow-hidden text-ellipsis">
              {confidential && !isVisible ? '•'.repeat(8) : label}
            </div>
            {confidential && (
              <Button
                dataTestId={dataTestId + '-toggle-visibility'}
                size="fit"
                variant="ghost"
                className="ml-auto"
                onClick={(e) => {
                  e.stopPropagation();
                  setIsVisible((prev) => !prev);
                }}>
                {isVisible ? (
                  <EyeSlashIcon className="h-5 w-5" />
                ) : (
                  <EyeIcon className="h-5 w-5" />
                )}
              </Button>
            )}
            <div>
              <span className="sr-only">Copy</span>
              {hasCopied ? (
                <CheckIcon className="h-4 w-4 text-green-600" />
              ) : (
                <CopyIcon className="h-4 w-4 rotate-90" />
              )}
            </div>
          </Button>
        </TooltipTrigger>
        <TooltipContent>Copied!</TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};

export default CopyTextButton;
