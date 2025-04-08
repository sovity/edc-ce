/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import Link from 'next/link';
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from '@/components/ui/breadcrumb';
import {useBreadcrumbStore} from '@/lib/stores/breadcrumb-store';

const BreadcrumbComponent = () => {
  const {items} = useBreadcrumbStore();

  return (
    <Breadcrumb>
      <BreadcrumbList>
        {items.map((x, i) =>
          i === items.length - 1 ? (
            <BreadcrumbItem key={x.label}>
              <BreadcrumbPage>{x.label}</BreadcrumbPage>
            </BreadcrumbItem>
          ) : (
            <div key={x.label} className="flex items-center gap-2">
              <BreadcrumbItem>
                {x.href ? (
                  <Link href={x.href}>{x.label}</Link>
                ) : (
                  <BreadcrumbPage className="text-muted-foreground">
                    {x.label}
                  </BreadcrumbPage>
                )}
              </BreadcrumbItem>
              <BreadcrumbSeparator />
            </div>
          ),
        )}
      </BreadcrumbList>
    </Breadcrumb>
  );
};

export default BreadcrumbComponent;
