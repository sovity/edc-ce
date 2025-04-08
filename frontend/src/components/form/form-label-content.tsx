/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {QuestionMarkCircleIcon} from '@heroicons/react/24/outline';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '../ui/tooltip';

export interface FormLabelContentProps {
  label: string;
  tooltip?: string;
  isRequired?: boolean;
}

const FormLabelContent = (data: FormLabelContentProps) => {
  return (
    <>
      {data.tooltip ? (
        <TooltipProvider>
          <Tooltip delayDuration={100}>
            <TooltipTrigger asChild>
              <div className="flex w-fit cursor-pointer items-center gap-2">
                {data.label} {data.isRequired && '*'}
                <QuestionMarkCircleIcon className="h-4 w-4 cursor-pointer text-blue-500" />
              </div>
            </TooltipTrigger>
            <TooltipContent className="font-normal">
              {data.tooltip}
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      ) : (
        <>
          {data.label} {data.isRequired && '*'}
        </>
      )}
    </>
  );
};

export default FormLabelContent;
