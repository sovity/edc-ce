/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {PolicyOperatorConfig} from '@/components/policy-editor/model/policy-operator-config';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {useTranslations} from 'next-intl';

export const PolicyConstraintOperatorLabel = ({
  operator,
}: {
  operator: PolicyOperatorConfig;
}) => {
  const t = useTranslations();
  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent delayDuration={100}>
        <TooltipTrigger asChild>
          <span>{operator.title(t)}</span>
        </TooltipTrigger>
        <TooltipContent className="text-sm font-normal">
          <h4 className="text-sm font-bold">{operator.title(t)}</h4>
          <p className="text-sm">{operator.description(t)}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};
