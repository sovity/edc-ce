/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {InformationCircleIcon} from '@heroicons/react/24/outline';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from './ui/tooltip';

const ExactMatchTooltip = () => {
  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent>
        <TooltipTrigger>
          <InformationCircleIcon className="h-6 w-6 text-gray-700" />
        </TooltipTrigger>
        <TooltipContent>
          <p>
            You can search for exact matches by enclosing your search in
            quotation marks (e.g. &quot;Exact Match&quot;)
          </p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};

export default ExactMatchTooltip;
