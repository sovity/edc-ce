/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Card, CardContent} from '@/components/ui/card';

export const StatCard = ({title, value}: {title: string; value: number}) => {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="text-center">
          <p className="mb-2 text-4xl font-bold">{value}</p>
          <p className="text-xs text-muted-foreground">{title}</p>
        </div>
      </CardContent>
    </Card>
  );
};
