/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {BlobServiceClient} from '@azure/storage-blob';

class AzureBlobStorageService {
  public static async getContainers(
    sasToken: string,
    storageAccountName: string,
  ): Promise<string[]> {
    const sasUrl =
      'https://' + storageAccountName + '.blob.core.windows.net?' + sasToken;
    const blobServiceClient = new BlobServiceClient(sasUrl);

    const containerNames: string[] = [];
    for await (const containerItem of blobServiceClient.listContainers()) {
      containerNames.push(containerItem.name);
    }
    return containerNames;
  }

  public static async getBlobs(
    sasToken: string,
    containerName: string,
    storageAccountName: string,
  ): Promise<string[]> {
    const containerUrl =
      'https://' +
      storageAccountName +
      '.blob.core.windows.net/' +
      containerName +
      '?' +
      sasToken;
    const blobServiceClient = new BlobServiceClient(containerUrl);
    const containerClient = blobServiceClient.getContainerClient('');

    const blobList = [];
    for await (const blob of containerClient.listBlobsFlat()) {
      blobList.push(blob.name);
    }

    return blobList;
  }
}

export default AzureBlobStorageService;
