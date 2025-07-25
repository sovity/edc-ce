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
import {editBusinessPartnerGroupFormSchema} from '../../[id]/edit/components/use-edit-business-partner-group-form';

export const createBusinessPartnerGroupFormSchema = z.intersection(
  z.object({
    groupId: z.string().min(1, 'Group ID is required'),
  }),
  editBusinessPartnerGroupFormSchema,
);

export type CreateBusinessPartnerGroupForm = z.infer<
  typeof createBusinessPartnerGroupFormSchema
>;

export const useCreateBusinessPartnerGroupForm = () => {
  const form = useForm<CreateBusinessPartnerGroupForm>({
    mode: 'all',
    resolver: zodResolver(createBusinessPartnerGroupFormSchema),
    defaultValues: {
      members: [],
    },
  });

  return {
    form,
  };
};
