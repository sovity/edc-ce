/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import React, {useEffect, useState} from 'react';
import {useForm} from 'react-hook-form';
import {z} from 'zod';
import {zodResolver} from '@hookform/resolvers/zod';
import {
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from '@/components/ui/form';
import AzureBlobStorageService from './azure-blob-storage-service';
import {Input} from '@/components/ui/input';
import {Button} from '@/components/ui/button';
import {Textarea} from '@/components/ui/textarea';

interface SelectorProps {
  setSelectionAllowed: (selectionAllowed: boolean) => void;
  setAzureDataAddress: (json: string) => void;
  selectionAllowed: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

const azureBlobSchema = z.object({
  storageSasToken: z.string().min(1, 'Storage SAS Token is required'),
  storageAccountName: z.string().min(1, 'Storage Account Name is required'),
  containerSasToken: z.string().min(1, 'Container SAS Token is required'),
  containerName: z.string().min(1, 'Container Name is required'),
  blobName: z.string().min(1, 'Blob Name is required'),
  azureStorageKeyName: z.string().min(1, 'Azure Storage Key Name is required'),
});

type AzureBlobFormData = z.infer<typeof azureBlobSchema>;

interface SelectorProps {
  setSelectionAllowed: (selectionAllowed: boolean) => void;
  setAzureDataAddress: (json: string) => void;
  selectionAllowed: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

export const AzureBlobSelector = ({
  setSelectionAllowed,
  setAzureDataAddress,
  selectionAllowed,
  setIsOpen,
}: SelectorProps) => {
  const [containers, setContainers] = useState<string[]>([]);
  const [blobs, setBlobs] = useState<string[]>([]);

  const form = useForm<AzureBlobFormData>({
    resolver: zodResolver(azureBlobSchema),
    defaultValues: {
      storageSasToken: '',
      storageAccountName: '',
      containerSasToken: '',
      containerName: '',
      blobName: '',
      azureStorageKeyName: '',
    },
  });

  const {watch} = form;
  const storageSasToken = watch('storageSasToken');
  const storageAccountName = watch('storageAccountName');
  const containerSasToken = watch('containerSasToken');
  const containerName = watch('containerName');
  const blobName = watch('blobName');
  const azureStorageKeyName = watch('azureStorageKeyName');

  // --- Backend calls ---
  const receiveContainers = async () => {
    const containerNames = await AzureBlobStorageService.getContainers(
      storageSasToken,
      storageAccountName,
    );
    setContainers(containerNames);
  };

  const receiveBlobs = async () => {
    const blobNames = await AzureBlobStorageService.getBlobs(
      containerSasToken,
      containerName,
      storageAccountName,
    );
    setBlobs(blobNames);
  };

  // --- Submit handler ---
  const onSubmit = (values: AzureBlobFormData) => {
    const obj = {
      type: 'AzureStorage',
      account: values.storageAccountName,
      container: values.containerName,
      blobName: values.blobName,
      keyName: values.azureStorageKeyName,
    };
    setAzureDataAddress(JSON.stringify(obj));
    setIsOpen(false);
  };

  // --- Determine if selection is allowed ---
  useEffect(() => {
    const allFilled =
      storageSasToken &&
      storageAccountName &&
      containerSasToken &&
      containerName &&
      blobName &&
      azureStorageKeyName;
    setSelectionAllowed(!!allFilled);
  }, [
    storageSasToken,
    storageAccountName,
    containerSasToken,
    containerName,
    blobName,
    setSelectionAllowed,
    azureStorageKeyName,
  ]);

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <DialogHeader>
          <DialogTitle>Select Azure Blob</DialogTitle>
        </DialogHeader>

        {/* Azure Storage Key Name */}
        <FormField
          control={form.control}
          name="azureStorageKeyName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Azure Storage Key Name</FormLabel>
              <FormControl>
                <Input placeholder="Azure Storage Key Name" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Divider */}
        <hr className="border-t border-muted my-4" />

        {/* Storage SAS Token + Account Name */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <FormField
            control={form.control}
            name="storageSasToken"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Storage Account SAS Token</FormLabel>
                <FormControl>
                  <Input placeholder="Storage SAS Token" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="storageAccountName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Storage Account Name</FormLabel>
                <FormControl>
                  <Input placeholder="Storage Account Name" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* List Containers */}
        <div className="space-y-2">
          <Button
            type="button"
            dataTestId="btn-receive-containers"
            disabled={!storageSasToken.length || !storageAccountName.length}
            onClick={receiveContainers}
          >
            List Containers
          </Button>
          {containers.length > 0 && (
            <ul className="border rounded-md divide-y max-h-40 overflow-auto">
              {containers.map((name) => (
                <li
                  key={name}
                  className={`p-2 cursor-pointer hover:bg-muted ${
                    containerName === name ? 'bg-muted font-semibold' : ''
                  }`}
                  onClick={() => form.setValue('containerName', name)}
                >
                  {name}
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Divider */}
        <hr className="border-t border-muted my-4" />

        {/* SAS Token + Container Name */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <FormField
            control={form.control}
            name="containerSasToken"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Container SAS Token</FormLabel>
                <FormControl>
                  <Input placeholder="Container SAS Token" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="containerName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Container Name</FormLabel>
                <FormControl>
                  <Input
                    placeholder="Container Name"
                    {...field}
                    value={field.value}
                    readOnly
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* List Blobs */}
        <div className="space-y-2">
          <Button
            type="button"
            dataTestId="btn-receive-blobs"
            disabled={
              !containerSasToken.length || !containerName.length || !storageAccountName.length
            }
            onClick={receiveBlobs}
          >
            List Blobs
          </Button>
          {blobs.length > 0 && (
            <ul className="border rounded-md divide-y max-h-40 overflow-auto">
              {blobs.map((name) => (
                <li
                  key={name}
                  className={`p-2 cursor-pointer hover:bg-muted ${
                    blobName === name ? 'bg-muted font-semibold' : ''
                  }`}
                  onClick={() => form.setValue('blobName', name)}
                >
                  {name}
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Divider */}
        <hr className="border-t border-muted my-4" />

        {/* Blob Name */}
        <FormField
          control={form.control}
          name="blobName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Blob Name</FormLabel>
              <FormControl>
                <Input placeholder="Blob Name" {...field} readOnly />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            dataTestId="cancel-blob-select"
            onClick={() => setIsOpen(false)}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            dataTestId="submit-blob-select"
            disabled={!selectionAllowed}
          >
            Select Blob
          </Button>
        </DialogFooter>
      </form>
    </Form>
  );
};

export default AzureBlobSelector;
