/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Button} from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {MoreHorizontal} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {type UiDataOffer} from '@sovity.de/edc-client';
import Link from 'next/link';
import {urls} from '@/lib/urls';

const AssetActionMenu = ({dataOffer}: {dataOffer: UiDataOffer}) => {
  const t = useTranslations();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          dataTestId={`btn-dropdown-data-offer-${dataOffer.asset.assetId}`}
          variant="ghost"
          className="h-8 w-8 p-0">
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" onClick={(e) => e.stopPropagation()}>
        <DropdownMenuItem asChild>
          <Link
            href={urls.catalog.detailPage(
              dataOffer.participantId,
              dataOffer.endpoint,
              dataOffer.asset.assetId,
            )}>
            {t('General.viewDetails')}
          </Link>
        </DropdownMenuItem>
        {dataOffer.asset.dataSourceAvailability === 'LIVE' && (
          <DropdownMenuItem asChild>
            <Link
              href={urls.catalog.detailPage(
                dataOffer.participantId,
                dataOffer.endpoint,
                dataOffer.asset.assetId,
                'contract-offers',
              )}>
              {t('General.negotiate')}
            </Link>
          </DropdownMenuItem>
        )}
        {dataOffer.asset.dataSourceAvailability === 'ON_REQUEST' && (
          <DropdownMenuItem asChild>
            <Link
              href={urls.catalog.detailPage(
                dataOffer.participantId,
                dataOffer.endpoint,
                dataOffer.asset.assetId,
                'properties',
              )}>
              {t('General.contact')}
            </Link>
          </DropdownMenuItem>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default AssetActionMenu;
