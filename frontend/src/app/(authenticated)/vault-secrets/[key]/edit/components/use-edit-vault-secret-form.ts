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

export const editVaultSecretFormSchema = z.object({
  editValue: z.boolean(),
  value: z.string().min(1).optional(),
  description: z.string().min(1),
});

export type EditVaultSecretForm = z.infer<typeof editVaultSecretFormSchema>;

export const useEditVaultSecretForm = (description: string) => {
  const form = useForm<EditVaultSecretForm>({
    mode: 'all',
    resolver: zodResolver(editVaultSecretFormSchema),
    defaultValues: {
      description,
      editValue: false,
    },
  });

  return {
    form,
  };
};
