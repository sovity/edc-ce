/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import type {VaultSecretListPageEntry} from '@sovity.de/edc-client';

export namespace TestSecrets {
  export const firstSecret: VaultSecretListPageEntry = {
    key: 'custom-secret',
    description:
      'This is the description for the first secret. It contains some more information on how this secret is used',
    updatedAt: new Date(),
  };

  export const buildGenericSecret = (
    idx: number,
  ): VaultSecretListPageEntry => ({
    key: `secret-${idx}`,
    description: `Description for secret ${idx}`,
    updatedAt: new Date(
      new Date(2025, 1, 1).getTime() + idx * (1000 * 60 * 60 * 24),
    ),
  });
}
