/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import type {PolicyExpressionMapped} from '@/components/policy-editor/model/policy-expression-mapped';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {useTranslations} from 'next-intl';

export const PolicyConstraintValueText = ({
  expression,
}: {
  expression: PolicyExpressionMapped;
}) => {
  const t = useTranslations();
  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent delayDuration={100}>
        <TooltipTrigger asChild>
          <span className="break-all">{expression.displayValue ?? 'null'}</span>
        </TooltipTrigger>
        <TooltipContent className="text-sm font-normal">
          <h4 className="text-sm font-bold">{t('General.rawJson')}</h4>
          <p className="whitespace-pre text-sm">{expression.valueJson}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};
