/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import Markdown from '@/components/markdown';
import {Alert, AlertDescription, AlertTitle} from '@/components/ui/alert';
import {Badge} from '@/components/ui/badge';
import {Card, CardContent, CardHeader} from '@/components/ui/card';
import {type UiAsset} from '@/lib/api/client/generated';
import {ContactIcon} from 'lucide-react';
import {useTranslations} from 'next-intl';

export interface UiAssetProps {
  data: UiAsset;
}

export const AssetDetailOverviewCard = ({data}: UiAssetProps) => {
  const t = useTranslations();

  const isOnRequestAsset = data.dataSourceAvailability === 'ON_REQUEST';

  return (
    <div className="space-y-4">
      {isOnRequestAsset && (
        <Alert>
          <ContactIcon className="h-4 w-4" />
          <AlertTitle>{t('Pages.AssetDetails.onRequestDataOffer')}</AlertTitle>
          <AlertDescription>
            {t('Pages.AssetDetails.onRequestDataOfferDescription')}
          </AlertDescription>
        </Alert>
      )}
      <Card className="w-full">
        {data.keywords?.length ? (
          <CardHeader className="border-b py-3">
            <div className="flex flex-wrap gap-3">
              {data.version ? (
                <Badge variant="default" className="text-xs">
                  {data.version}
                </Badge>
              ) : null}
              {data.keywords?.map((tag, index) => (
                <Badge key={index} variant="outline" className="text-xs">
                  {tag}
                </Badge>
              ))}
            </div>
          </CardHeader>
        ) : null}
        <CardContent className="py-6">
          {data.description ? (
            <Markdown markdown={data.description} />
          ) : (
            <div className="text-sm italic text-gray-500">
              {t('Pages.AssetDetails.noDescription')}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
