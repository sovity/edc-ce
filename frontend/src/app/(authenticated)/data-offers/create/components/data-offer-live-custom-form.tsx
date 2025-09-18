/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import TextareaField from '@/components/form/textarea-field';
import {type DataOfferLiveType} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {jsonString} from '@/lib/utils/zod/schema-utils';
import {useTranslations} from 'next-intl';
import {type UseFormReturn} from 'react-hook-form';
import {Button} from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle
} from "@/components/ui/dialog";
import {z} from 'zod';
import {useEffect, useState} from "react";
import AzureBlobSelector from "@/components/azure-blob-selector/azure-blob-selector";

export const dataOfferLiveCustomSchema = z.object({
  offerLiveType: z.literal('CUSTOM_JSON' satisfies DataOfferLiveType),
  dataAddressJson: jsonString(),
});

export type DataOfferLiveCustomFormValue = z.infer<
  typeof dataOfferLiveCustomSchema
>;

export const DataOfferLiveCustomForm = ({
  form,
  formKeyDataOfferTypeLive,
}: {
  form: UseFormReturn<any>;
  formKeyDataOfferTypeLive: string;
}) => {
  const t = useTranslations();

  const [isAzureOpen, setIsAzureOpen] = useState(false);
  const [selectionAllowed, setSelectionAllowed] = useState(false);
  const [azureDataAddress, setAzureDataAddress] = useState("");

  const fieldKey = (key: string): string =>
    formKeyDataOfferTypeLive === ''
      ? formKeyDataOfferTypeLive
      : `${formKeyDataOfferTypeLive}.${key}`;

  const value = form.watch(
    formKeyDataOfferTypeLive,
  ) as DataOfferLiveCustomFormValue;

  useEffect(() => {
    console.log(`azureDataAddress: ${azureDataAddress}`);
    if (azureDataAddress) {
      form.setValue(fieldKey('dataAddressJson'), azureDataAddress);
    }
  }, [azureDataAddress]);

  return (
    value.offerLiveType === 'CUSTOM_JSON' && (
      <>
        {/* Custom Data Address JSON */}
        <TextareaField
          control={form.control}
          name={fieldKey('dataAddressJson')}
          placeholder='{"https://w3id.org/edc/v0.0.1/ns/type": "HttpData", ...}'
          label={t('Pages.DataOfferCreate.dataSourceTypeCustom')}
        />
        <Button
          dataTestId={'btn-open-azure-blob-select'}
          type="button"
          onClick={() => setIsAzureOpen(true)}
        >
          Select Blob
        </Button>
        <Dialog open={isAzureOpen} onOpenChange={(open) => !open}>
          <DialogContent className="w-auto">
            <DialogHeader>
              <DialogTitle>Select Blob</DialogTitle>
              <DialogDescription>
                <AzureBlobSelector
                  setSelectionAllowed={setSelectionAllowed}
                  setAzureDataAddress={setAzureDataAddress}
                  selectionAllowed={selectionAllowed}
                  setIsOpen={setIsAzureOpen}
                />
              </DialogDescription>
            </DialogHeader>
          </DialogContent>
        </Dialog>
      </>
    )
  );
};
