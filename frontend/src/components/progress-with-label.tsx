/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {cn} from '@/lib/utils/css-utils';
import {Progress} from './ui/progress';

const ProgressWithLabel = ({
  value,
  max,
  className,
}: {
  value: number;
  max: number;
  className?: string;
}) => {
  return (
    <div className={cn('flex gap-2', className)}>
      <Progress className="flex-grow self-center" value={(value / max) * 100} />
      <div className="text-nowrap">
        {value} / {max}
      </div>
    </div>
  );
};

export default ProgressWithLabel;
