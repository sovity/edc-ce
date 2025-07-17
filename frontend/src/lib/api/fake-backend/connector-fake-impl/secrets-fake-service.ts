/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import type {
  IdResponseDto,
  VaultSecretCreateSubmit,
  VaultSecretEditPage,
  VaultSecretEditSubmit,
  VaultSecretListPageEntry,
  VaultSecretQuery,
} from '@sovity.de/edc-client';
import {TestSecrets} from '@/lib/api/fake-backend/connector-fake-impl/data/test-secrets';

let secrets: VaultSecretListPageEntry[] = [
  TestSecrets.firstSecret,
  ...[...new Array(20).keys()].map((i) => TestSecrets.buildGenericSecret(i)),
];

export const listVaultSecretsPage = (
  query?: VaultSecretQuery,
): VaultSecretListPageEntry[] => {
  const filteredSecrets = secrets.filter(
    (s) =>
      !query?.searchQuery ||
      s.key.includes(query.searchQuery) ||
      s.description.includes(query.searchQuery),
  );
  if (query?.limit === undefined) {
    return filteredSecrets;
  } else {
    return filteredSecrets.slice(0, query.limit);
  }
};

export const deleteVaultSecret = (key: string): IdResponseDto => {
  secrets = secrets.filter((s) => s.key !== key);

  return {id: key, lastUpdatedDate: new Date()};
};

export const createVaultSecret = (
  secret: VaultSecretCreateSubmit,
): IdResponseDto => {
  secrets = [
    ...secrets,
    {key: secret.key, description: secret.description, updatedAt: new Date()},
  ];

  return {id: secret.key, lastUpdatedDate: new Date()};
};

export const editVaultSecretPage = (key: string): VaultSecretEditPage => {
  const secret = secrets.find((s) => s.key === key);
  if (!secret) {
    throw new Error(`Secret with key ${key} not found`);
  }

  return {
    key: secret.key,
    description: secret.description,
  };
};

export const editVaultSecret = (
  key: string,
  submitRequest: VaultSecretEditSubmit,
): IdResponseDto => {
  secrets = secrets.map((s) => {
    if (s.key === key) {
      return {
        ...s,
        description: submitRequest.description,
      };
    } else {
      return s;
    }
  });
  return {
    id: key,
    lastUpdatedDate: new Date(),
  };
};
