/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {PolicyVerbConfig} from '@/components/policy-editor/model/policy-verb-config';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {useTranslations} from 'next-intl';

export const PolicyConstraintVerbLabel = ({
  verb,
  className,
}: {
  verb: PolicyVerbConfig;
  className?: string;
}) => {
  const t = useTranslations();
  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent delayDuration={100}>
        <TooltipTrigger asChild>
          <span className={className}>{verb.operandLeftTitle(t)}</span>
        </TooltipTrigger>
        <TooltipContent className="text-sm font-normal">
          <h4 className="text-sm font-bold">{verb.operandLeftTitle(t)}</h4>
          <p className="text-sm">{verb.operandLeftDescription(t)}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};
