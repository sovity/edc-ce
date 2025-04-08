/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {Card, CardContent} from '@/components/ui/card';
import {ArrowUpDown, ChevronRight} from 'lucide-react';

export const AssetDetailsTransfers = () => {
  return (
    <Card>
      <CardContent className="p-6">
        <div className="space-y-4">
          <div className="flex items-start gap-4 rounded-lg border p-4">
            <ArrowUpDown className="mt-0.5 h-5 w-5 text-muted-foreground" />
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <p className="font-medium">ALMOST 2 YEARS AGO · IN_PROGRESS</p>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </div>
              <p className="mt-1 text-sm text-muted-foreground">
                2679d234-3340-44bf-a96b-c88b57838033
              </p>
            </div>
          </div>

          <div className="flex items-start gap-4 rounded-lg border p-4">
            <ArrowUpDown className="mt-0.5 h-5 w-5 text-muted-foreground" />
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <p className="font-medium">ALMOST 2 YEARS AGO · COMPLETED</p>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </div>
              <p className="mt-1 text-sm text-muted-foreground">
                c2863791-c8f3-49e7-8137-7fadaa36b4e4
              </p>
            </div>
          </div>

          <div className="flex items-start gap-4 rounded-lg border p-4">
            <ArrowUpDown className="mt-0.5 h-5 w-5 text-muted-foreground" />
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <p className="font-medium">
                  ALMOST 2 YEARS AGO · DEPROVISIONING
                </p>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </div>
              <p className="mt-1 text-sm text-muted-foreground">
                f3ee5129-1909-4d7b-a6fe-a25994d67b56
              </p>
            </div>
          </div>

          <div className="flex items-start gap-4 rounded-lg border bg-red-50 p-4">
            <ArrowUpDown className="mt-0.5 h-5 w-5 text-red-500" />
            <div className="flex-1">
              <div className="flex items-center justify-between">
                <p className="font-medium text-red-600">
                  ALMOST 2 YEARS AGO · ERROR
                </p>
                <ChevronRight className="h-5 w-5 text-red-500" />
              </div>
              <p className="mt-1 text-sm text-red-500">
                2cf2c9be-3b8c-4768-b10e-c1d9f9874e62
              </p>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
