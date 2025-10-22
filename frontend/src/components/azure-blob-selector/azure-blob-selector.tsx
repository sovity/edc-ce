/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import React, {useEffect, useState} from 'react';
import AzureBlobStorageService from "./azure-blob-storage-service";
import {Card, CardContent} from "@/components/ui/card";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {Textarea} from "@/components/ui/textarea";

interface SelectorProps {
  setSelectionAllowed: (selectionAllowed: boolean) => void,
  setAzureDataAddress: (json: string) => void,
  selectionAllowed: boolean,
  setIsOpen: (isOpen: boolean) => void
}

const AzureBlobSelector = ({
  setSelectionAllowed,
  setAzureDataAddress,
  selectionAllowed,
  setIsOpen
}: SelectorProps) => {

  const [sasToken, setSasToken] = useState("");
  const [storageSasToken, setStorageSasToken] = useState("");
  const [storageAccountName, setStorageAccountName] = useState("");
  const [containerName, setContainerName] = useState("");
  const [blobName, setBlobName] = useState("");
  const [containers, setContainers] = useState<string[]>([]);
  const [blobs, setBlobs] = useState<string[]>([]);

  const handleChangeSasToken = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSasToken(event.target.value);
  };

  const handleStorageSasToken = (event: React.ChangeEvent<HTMLInputElement>) => {
    setStorageSasToken(event.target.value);
  };

  const handleChangeStorageAccountName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setStorageAccountName(event.target.value);
  };

  const handleChangeContainerName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setContainerName(event.target.value);
  };

  const handleChangeBlobName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setBlobName(event.target.value);
  };

  const receiveContainers = async () => {
    await AzureBlobStorageService.getContainers(storageSasToken, storageAccountName).then((containerNames) => {
      setContainers(containerNames);
    })
  }

  const receiveBlobs = async () => {
    await AzureBlobStorageService.getBlobs(sasToken, containerName, storageAccountName).then((blobNames) => {
      setBlobs(blobNames);
    })
  }

  const clickSubmit = () => {
    createAzureDataAddress();
    setIsOpen(false);
  }

  const createAzureDataAddress = () => {
    const obj = {
      type: "AzureStorage",
      account: storageAccountName,
      container: containerName,
      blobName: blobName,
      keyName: "key1",
    };

    const res = JSON.stringify(obj);
    setAzureDataAddress(res);
  }

  useEffect(() => {
    if (sasToken && storageSasToken && storageAccountName && containerName && blobName) {
      setSelectionAllowed(true);
    } else {
      setSelectionAllowed(false);
    }
  }, [sasToken, storageSasToken, storageAccountName, containerName, blobName, setSelectionAllowed]);

  return (
    <div className="p-6 max-w-6xl mx-auto grid gap-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card>
          <CardContent className="p-4 space-y-2">
            <Input
              placeholder="Storage Account SAS Token"
              value={storageSasToken}
              onChange={handleStorageSasToken}
            />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4 space-y-2">
            <Input
              placeholder="Storage Account Name"
              value={storageAccountName}
              onChange={handleChangeStorageAccountName}
            />
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardContent className="p-4 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button
              dataTestId={'btn-receive-containers'}
              disabled={storageSasToken.length === 0 || storageAccountName.length === 0}
              onClick={receiveContainers}
            >
              List Containers
            </Button>

            <Textarea
              placeholder="Available Azure Blob Containers"
              className="col-span-2"
              disabled
              value={containers}
            />
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card>
          <CardContent className="p-4 space-y-2">
            <Input
              placeholder="SAS Token"
              value={sasToken}
              onChange={handleChangeSasToken}
            />
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4 space-y-2">
            <Input
              placeholder="Container Name"
              value={containerName}
              onChange={handleChangeContainerName}
            />
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardContent className="p-4 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button
              dataTestId={'btn-receive-blobs'}
              disabled={
                sasToken.length === 0 ||
                containerName.length === 0 ||
                storageAccountName.length === 0
              }
              onClick={receiveBlobs}
            >
              List Blobs
            </Button>

            <Textarea
              placeholder="Blobs"
              className="col-span-2"
              disabled
              value={blobs}
            />
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-4 space-y-2">
          <Input
            placeholder="Blob Name"
            value={blobName}
            onChange={handleChangeBlobName}
          />
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-4 space-y-2">
          <Button
            type="button"
            variant="outline"
            dataTestId={'cancel-blob-select'}
            onClick={() => setIsOpen(false)}
          >
            Cancel
          </Button>
          <Button
            type="button"
            dataTestId={'submit-blob-select'}
            disabled={!selectionAllowed}
            onClick={() => clickSubmit()}
          >
            Select Blob
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}

export default AzureBlobSelector;
