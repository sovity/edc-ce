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

export const editBusinessPartnerGroupFormSchema = z.object({
  members: z.array(z.string()).min(1, 'At least one member is required'),
});

export type EditBusinessPartnerGroupForm = z.infer<
  typeof editBusinessPartnerGroupFormSchema
>;

export const useEditBusinessPartnerGroupForm = (members: string[]) => {
  const form = useForm<EditBusinessPartnerGroupForm>({
    mode: 'all',
    resolver: zodResolver(editBusinessPartnerGroupFormSchema),
    defaultValues: {members},
  });

  return {
    form,
  };
};
