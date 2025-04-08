/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  type ContractAgreementDirection,
  type ContractTerminationStatus,
} from '@sovity.de/edc-client';
import {CloudDownload, CloudUpload, MoveRight, Slash} from 'lucide-react';
import {cn} from '@/lib/utils/css-utils';
import * as React from 'react';
import {Badge} from '@/components/ui/badge';
import {urls} from '@/lib/urls';
import PageTitleStackStyle from '@/components/stacks/common-styles/page-title-stack-style';
import TableCellStackStyle from '@/components/stacks/common-styles/table-cell-stack-style';

export default function ContractAgreementHeaderStack(props: {
  children?: React.ReactNode;
  className?: string;
  contractAgreementId: string;
  direction: ContractAgreementDirection;
  terminationStatus: ContractTerminationStatus;
  assetName: string;
  counterpartyParticipantId: string;
  size: 'page-title' | 'table-cell';
}) {
  const {
    children,
    className,
    contractAgreementId,
    assetName,
    counterpartyParticipantId,
    direction,
    terminationStatus,
    size,
  } = props;

  const iconSize = size === 'page-title' ? 'size-8' : 'size-6';
  const Icon = direction === 'PROVIDING' ? CloudUpload : CloudDownload;
  const icon =
    terminationStatus === 'TERMINATED' ? (
      <span className={cn('relative inline-block shrink-0', iconSize)}>
        <Icon className={iconSize} />
        <Slash
          className={cn('absolute bottom-0 right-0  stroke-red-500', iconSize)}
        />
      </span>
    ) : (
      <Icon className={cn(iconSize, 'shrink-0')} />
    );

  const connectionIconSize = size === 'page-title' ? 'size-4' : 'size-3';
  const connection =
    direction === 'CONSUMING' ? (
      <div className="flex items-center gap-2">
        <span>{counterpartyParticipantId}</span>
        <MoveRight className={connectionIconSize} />
        <span>You</span>
      </div>
    ) : (
      <div className="flex items-center gap-2">
        <span>You</span>
        <MoveRight className={connectionIconSize} />
        <span>{counterpartyParticipantId}</span>
      </div>
    );

  if (size === 'page-title') {
    return (
      <PageTitleStackStyle
        className={className}
        icon={icon}
        title={assetName}
        titleSiblings={
          terminationStatus === 'TERMINATED' && (
            <Badge className={'text-xs'} variant={'outline'}>
              Terminated
            </Badge>
          )
        }
        subtitle={connection}>
        {children}
      </PageTitleStackStyle>
    );
  }

  return (
    <TableCellStackStyle
      href={urls.contracts.detailPage(contractAgreementId)}
      dataTestId={`link-contract-${contractAgreementId}`}
      className={className}
      icon={icon}
      title={assetName}
      subtitle={connection}>
      {children}
    </TableCellStackStyle>
  );
}
