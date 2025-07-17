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
import {UiCriterionOperator} from '@sovity.de/edc-client';
import {TranslatedString} from '@/lib/utils/translation-utils';

interface AssetSelectorOperatorDef {
  title: TranslatedString;
  description: TranslatedString;
}

const OPERATOR_DEFS: Record<UiCriterionOperator, AssetSelectorOperatorDef> = {
  EQ: {
    title: (t) => t('General.AssetSelector.Operator.eqTitle'),
    description: (t) => t('General.AssetSelector.Operator.eqDescription'),
  },
  IN: {
    title: (t) => t('General.AssetSelector.Operator.inTitle'),
    description: (t) => t('General.AssetSelector.Operator.inDescription'),
  },
  LIKE: {
    title: (t) => t('General.AssetSelector.Operator.likeTitle'),
    description: (t) => t('General.AssetSelector.Operator.likeDescription'),
  },
};

export const AssetSelectorOperatorLabel = ({
  operator,
}: {
  operator: UiCriterionOperator;
}) => {
  const t = useTranslations();
  const def = OPERATOR_DEFS[operator];
  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent delayDuration={100}>
        <TooltipTrigger asChild>
          <span>{def.title(t)}</span>
        </TooltipTrigger>
        <TooltipContent className="text-sm font-normal">
          <h4 className="text-sm font-bold">{def.title(t)}</h4>
          <p className="text-sm">{def.description(t)}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};
