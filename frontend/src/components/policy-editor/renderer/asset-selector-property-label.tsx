/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import {useTranslations} from 'next-intl';

export const ASSET_ID_PROPERTY_NAME = 'https://w3id.org/edc/v0.0.1/ns/id';

/**
 * Displays an asset property nicely, if known, e.g. Asset ID
 */
export const AssetSelectorPropertyLabel = ({
  assetProperty,
  className,
}: {
  assetProperty: string;
  className?: string;
}) => {
  const t = useTranslations();

  const label =
    assetProperty === ASSET_ID_PROPERTY_NAME
      ? t('General.asset')
      : assetProperty;

  const isCustomProperty = assetProperty !== ASSET_ID_PROPERTY_NAME;

  return (
    <TooltipProvider>
      <Tooltip disableHoverableContent delayDuration={100}>
        <TooltipTrigger asChild>
          <span className={className}>{label}</span>
        </TooltipTrigger>
        <TooltipContent className="text-sm font-normal">
          <h4 className="text-sm font-bold">
            {isCustomProperty ? t('General.customProperty') : label}
          </h4>
          <p className="text-sm">{assetProperty}</p>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
};
