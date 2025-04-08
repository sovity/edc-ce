/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {cn} from '@/lib/utils/css-utils';
import ContactSupport from './contact-support';
import {Button} from './ui/button';

export interface InfoWithReloadActionProps {
  title: string;
}

const InfoWithReloadAction = ({
  title,
  className,
  ...props
}: InfoWithReloadActionProps & React.HTMLAttributes<HTMLDivElement>) => {
  return (
    <div
      className={cn(
        'flex h-full flex-col items-center justify-center',
        className,
      )}
      {...props}>
      <div>{title}</div>
      <div className="mt-4 flex w-full items-center justify-center gap-x-6">
        <Button
          dataTestId={'refresh'}
          size="sm"
          onClick={() => window.location.reload()}>
          Refresh
        </Button>
        <ContactSupport />
      </div>
    </div>
  );
};

export default InfoWithReloadAction;
