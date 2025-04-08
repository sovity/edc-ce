/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Card, CardContent, CardHeader, CardTitle} from '@/components/ui/card';

export const AssetPropertyCard = ({
  title,
  children,
  footer,
}: {
  title: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
}) => {
  return (
    <Card>
      <CardHeader className="py-5">
        <CardTitle className="text-lg font-medium">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-1 gap-2 md:grid-cols-2 2xl:grid-cols-3">
          {children}
        </div>
        {footer && <div className="mt-10 flex justify-end gap-2">{footer}</div>}
      </CardContent>
    </Card>
  );
};
