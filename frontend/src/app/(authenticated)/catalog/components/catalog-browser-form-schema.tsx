/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect} from 'react';
import {zodResolver} from '@hookform/resolvers/zod';
import {useForm, type UseFormReturn} from 'react-hook-form';
import z from 'zod';

const catalogBrowserFormSchema = z.object({
  connectorEndpoint: z.string().url().min(1).max(512),
  participantId: z.string().min(1).max(50),
});

export type CatalogFormValue = z.infer<typeof catalogBrowserFormSchema>;

export const useCatalogBrowserForm = () => {
  const form = useForm<CatalogFormValue>({
    mode: 'onTouched',
    resolver: zodResolver(catalogBrowserFormSchema),
    defaultValues: {
      connectorEndpoint: '',
      participantId: '',
    },
  });

  useWithAutofillParticipantIdFromUrl(form);

  return {
    form,
    schema: catalogBrowserFormSchema,
  };
};

const useWithAutofillParticipantIdFromUrl = (
  form: UseFormReturn<CatalogFormValue>,
) => {
  const connectorEndpoint = form.watch('connectorEndpoint');
  const setValue = form.setValue;
  useEffect(() => {
    if (connectorEndpoint?.includes('?participantId=')) {
      const [endpointWithoutParticipantId, participantId] =
        connectorEndpoint.split('?participantId=');
      setValue('participantId', participantId!);
      setValue('connectorEndpoint', endpointWithoutParticipantId!);
    }
  }, [connectorEndpoint, setValue]);
};
