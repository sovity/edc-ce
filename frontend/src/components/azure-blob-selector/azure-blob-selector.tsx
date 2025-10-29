/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import React, {useEffect, useState} from 'react';
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormControl,
  FormMessage,
} from "@/components/ui/form";
import AzureBlobStorageService from "./azure-blob-storage-service";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Textarea} from "@/components/ui/textarea";

interface SelectorProps {
  setSelectionAllowed: (selectionAllowed: boolean) => void,
  setAzureDataAddress: (json: string) => void,
  selectionAllowed: boolean,
  setIsOpen: (isOpen: boolean) => void
}

const azureBlobSchema = z.object({
  storageSasToken: z.string().min(1, "Storage SAS Token is required"),
  storageAccountName: z.string().min(1, "Storage Account Name is required"),
  sasToken: z.string().min(1, "SAS Token is required"),
  containerName: z.string().min(1, "Container Name is required"),
  blobName: z.string().min(1, "Blob Name is required"),
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
      storageSasToken: "",
      storageAccountName: "",
      sasToken: "",
      containerName: "",
      blobName: "",
    },
  });

  const { watch } = form;
  const storageSasToken = watch("storageSasToken");
  const storageAccountName = watch("storageAccountName");
  const sasToken = watch("sasToken");
  const containerName = watch("containerName");
  const blobName = watch("blobName");

  // --- Backend calls ---
  const receiveContainers = async () => {
    const containerNames = await AzureBlobStorageService.getContainers(
      storageSasToken,
      storageAccountName
    );
    setContainers(containerNames);
  };

  const receiveBlobs = async () => {
    const blobNames = await AzureBlobStorageService.getBlobs(
      sasToken,
      containerName,
      storageAccountName
    );
    setBlobs(blobNames);
  };

  // --- Submit handler ---
  const onSubmit = (values: AzureBlobFormData) => {
    const obj = {
      type: "AzureStorage",
      account: values.storageAccountName,
      container: values.containerName,
      blobName: values.blobName,
      keyName: "key1",
    };
    setAzureDataAddress(JSON.stringify(obj));
    setIsOpen(false);
  };

  // --- Determine if selection is allowed ---
  useEffect(() => {
    const allFilled =
      storageSasToken &&
      storageAccountName &&
      sasToken &&
      containerName &&
      blobName;
    setSelectionAllowed(!!allFilled);
  }, [
    storageSasToken,
    storageAccountName,
    sasToken,
    containerName,
    blobName,
    setSelectionAllowed,
  ]);

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="space-y-4"
      >
        <DialogHeader>
          <DialogTitle>Select Azure Blob</DialogTitle>
        </DialogHeader>

        {/* Storage SAS Token + Account Name */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Button
            type="button"
            dataTestId="btn-receive-containers"
            disabled={!storageSasToken.length || !storageAccountName.length}
            onClick={receiveContainers}
          >
            List Containers
          </Button>
          <Textarea
            placeholder="Available Containers"
            className="col-span-2"
            disabled
            value={containers.join("\n")}
          />
        </div>

        {/* SAS Token + Container Name */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="sasToken"
            render={({ field }) => (
              <FormItem>
                <FormLabel>SAS Token</FormLabel>
                <FormControl>
                  <Input placeholder="SAS Token" {...field} />
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
                  <Input placeholder="Container Name" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* List Blobs */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Button
            type="button"
            dataTestId="btn-receive-blobs"
            disabled={
              !sasToken.length || !containerName.length || !storageAccountName.length
            }
            onClick={receiveBlobs}
          >
            List Blobs
          </Button>
          <Textarea
            placeholder="Blobs"
            className="col-span-2"
            disabled
            value={blobs.join("\n")}
          />
        </div>

        {/* Blob Name */}
        <FormField
          control={form.control}
          name="blobName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Blob Name</FormLabel>
              <FormControl>
                <Input placeholder="Blob Name" {...field} />
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
