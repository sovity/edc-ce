/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {useDialogsStore} from '@/lib/stores/dialog-store';
import {ScrollArea} from '../ui/scroll-area';
import {type CardPropertyType} from './card';
import CardProperty from './card-property';

const useShowAllPropertiesDialog = (
  id: string,
  title: string,
  subtitle: string,
  properties: CardPropertyType[],
) => {
  const {showDialog} = useDialogsStore();
  const dialogId = `all-properties-dialog-${id}`;

  return () => {
    showDialog({
      id: dialogId,
      dialogContent: () => (
        <AllPropertiesDialog
          properties={properties}
          id={id}
          title={title}
          subtitle={subtitle}
        />
      ),
    });
  };
};

const AllPropertiesDialog = ({
  properties,
  id,
  title,
  subtitle,
}: {
  properties: CardPropertyType[];
  id: string;
  title: string;
  subtitle: string;
}) => {
  return (
    <div className="grid gap-4">
      <DialogHeader>
        <DialogTitle>
          <div>
            <h3 className="pr-5 text-base font-semibold text-brand-darkblue">
              {title}
            </h3>
            <div className="text-sm text-muted-foreground">{subtitle}</div>
          </div>
        </DialogTitle>
      </DialogHeader>
      <DialogDescription asChild>
        <ScrollArea className="mt-3 max-h-80 rounded-md border py-4">
          <div className="grid grid-cols-1 gap-3 gap-y-6 px-5">
            {properties.map((property) => (
              <CardProperty
                key={`dialog-${id}-property-${property.name}`}
                name={property.name}
                Icon={property.icon}
                noLineClamp>
                {property.value}
              </CardProperty>
            ))}
          </div>
        </ScrollArea>
      </DialogDescription>
      <DialogFooter></DialogFooter>
    </div>
  );
};

export default useShowAllPropertiesDialog;
