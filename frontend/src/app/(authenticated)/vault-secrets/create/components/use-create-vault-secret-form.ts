/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {zodResolver} from '@hookform/resolvers/zod';
import {useForm} from 'react-hook-form';
import {z} from 'zod';

export const createVaultSecretFormSchema = z.object({
  key: z.string().min(1),
  value: z.string().min(1),
  description: z.string().min(1),
});

export type CreateVaultSecretForm = z.infer<typeof createVaultSecretFormSchema>;

export const useCreateVaultSecretForm = (key?: string) => {
  const form = useForm<CreateVaultSecretForm>({
    mode: 'all',
    resolver: zodResolver(createVaultSecretFormSchema),
    defaultValues: {key},
  });

  return {
    form,
  };
};
