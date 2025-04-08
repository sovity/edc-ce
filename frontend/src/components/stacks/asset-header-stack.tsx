/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {type DataSourceAvailability} from '@sovity.de/edc-client';
import {Database, MailPlus} from 'lucide-react';
import {cn} from '@/lib/utils/css-utils';
import * as React from 'react';
import {Badge} from '@/components/ui/badge';
import {urls} from '@/lib/urls';
import PageTitleStackStyle from '@/components/stacks/common-styles/page-title-stack-style';
import TableCellStackStyle from '@/components/stacks/common-styles/table-cell-stack-style';

export default function AssetHeaderStack(props: {
  children?: React.ReactNode;
  className?: string;
  assetId: string;
  assetName: string;
  dataSourceAvailability: DataSourceAvailability;
  size: 'page-title' | 'table-cell';
}) {
  const {
    children,
    size,
    className,
    assetId,
    assetName,
    dataSourceAvailability,
  } = props;

  const iconSize = size === 'page-title' ? 'size-7' : 'size-5';
  const Icon = dataSourceAvailability === 'LIVE' ? Database : MailPlus;
  const icon = <Icon className={cn(iconSize, 'shrink-0')} />;

  if (size === 'page-title') {
    return (
      <PageTitleStackStyle
        className={className}
        icon={icon}
        title={assetName}
        titleSiblings={
          dataSourceAvailability === 'ON_REQUEST' && (
            <Badge className={'whitespace-nowrap text-xs'} variant={'outline'}>
              On Request
            </Badge>
          )
        }
        subtitle={assetId}>
        {children}
      </PageTitleStackStyle>
    );
  }

  return (
    <TableCellStackStyle
      href={urls.assets.detailPage(assetId)}
      dataTestId={`link-asset-${assetId}`}
      className={className}
      icon={icon}
      title={assetName}
      subtitle={assetId}>
      {children}
    </TableCellStackStyle>
  );
}
