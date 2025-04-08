/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {dataOfferLiveSchema} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-form';
import {dataOfferOnRequestSchema} from '@/app/(authenticated)/data-offers/create/components/data-offer-on-request-form';
import {z} from 'zod';

export type DataOfferType = 'LIVE' | 'ON_REQUEST' | 'UNCHANGED';
export type DataOfferLiveType = 'HTTP' | 'CUSTOM_JSON';

export const dataOfferTypeSchema = z.discriminatedUnion('offerType', [
  z.object({
    offerType: z.literal('UNCHANGED'),
  }),
  dataOfferLiveSchema,
  dataOfferOnRequestSchema,
]);

export type DataOfferTypeFormValue = z.infer<typeof dataOfferTypeSchema>;
