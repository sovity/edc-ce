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
  left,
  className,
}: {
  verb: PolicyVerbConfig;
  /**
   * Raw left operand. Only needed for verbs with a dynamic left operand, e.g. "POLICY_CLAIM_COUNTRY"
   */
  left?: string;
  className?: string;
}) => {
  const t = useTranslations();
  const title = getVerbTitle(verb, left, t);
  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent delayDuration={100}>
        <TooltipTrigger asChild>
          <span className={className}>{title}</span>
        </TooltipTrigger>
        <TooltipContent className="text-sm font-normal">
          <h4 className="text-sm font-bold">{title}</h4>
          <p className="text-sm">{verb.operandLeftDescription(t)}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};

/**
 * Verbs with a dynamic left operand show the dynamic part, e.g. "Consumer's Token Claim: COUNTRY"
 */
const getVerbTitle = (
  verb: PolicyVerbConfig,
  left: string | undefined,
  t: ReturnType<typeof useTranslations>,
): string => {
  const title = verb.operandLeftTitle(t);
  const prefix = verb.operandLeftPrefix;
  if (prefix && left?.startsWith(prefix)) {
    const dynamicPart = left.substring(prefix.length);
    return dynamicPart ? `${title}: ${dynamicPart}` : title;
  }
  return title;
};
