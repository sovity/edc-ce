/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type ReactNode, useState} from 'react';
import {Button} from '@/components/ui/button';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {
  EyeIcon,
  EyeSlashIcon,
  QuestionMarkCircleIcon,
} from '@heroicons/react/24/outline';

export interface EntityAttributeProps {
  name: string;
  tooltip?: ReactNode;
  children: ReactNode;
  confidential?: boolean;
}

const EntityAttribute = ({
  name,
  tooltip,
  children,
  confidential,
}: EntityAttributeProps) => {
  const [visible, setVisible] = useState(false);

  if (!children) return null;

  return (
    <div className="px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-3">
      {tooltip ? (
        <TooltipProvider>
          <Tooltip delayDuration={100}>
            <TooltipTrigger asChild>
              <div className="flex w-fit cursor-pointer items-center gap-1">
                <dt className="text-sm leading-6 text-gray-500">{name}</dt>
                <QuestionMarkCircleIcon className="h-4 w-4 cursor-pointer text-blue-500" />
              </div>
            </TooltipTrigger>
            <TooltipContent>{tooltip}</TooltipContent>
          </Tooltip>
        </TooltipProvider>
      ) : (
        <dt className="text-sm leading-6 text-gray-500">{name}</dt>
      )}
      <dd className="mt-1 flex items-center gap-2 text-sm leading-6 text-accent-foreground sm:col-span-2 sm:mt-0">
        {confidential ? (
          <>
            <span className="">{visible ? children : '•'.repeat(8)}</span>
            <Button
              dataTestId={`btn-show-${name}`}
              type="button"
              variant="ghost"
              size="icon"
              onClick={() => setVisible(!visible)}>
              {visible ? (
                <EyeSlashIcon className="h-5 w-5" />
              ) : (
                <EyeIcon className="h-5 w-5" />
              )}
            </Button>
          </>
        ) : (
          children
        )}
      </dd>
    </div>
  );
};

export default EntityAttribute;
