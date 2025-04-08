/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {forwardRef} from 'react';
import {cn} from '@/lib/utils/css-utils';
import {getLocalDateTimeMinutesString} from '@/lib/utils/dates';
import ReactTimeago, {type ReactTimeagoProps} from 'react-timeago';

interface LocalTimeAgoProps extends ReactTimeagoProps<'time'> {
  date: Date;
  hideTooltip?: boolean;
}

const LocalTimeAgo = forwardRef(
  ({date, title, hideTooltip, ...props}: LocalTimeAgoProps, _) => {
    return (
      <ReactTimeago
        {...props}
        date={date}
        title={title ?? getLocalDateTimeMinutesString(date)}
        className={cn(hideTooltip && 'pointer-events-none')}
      />
    );
  },
);
LocalTimeAgo.displayName = 'LocalTimeAgo';

export default LocalTimeAgo;
