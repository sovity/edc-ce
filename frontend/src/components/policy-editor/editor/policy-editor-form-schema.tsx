/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {policyEditorConstraintFormSchema} from '@/components/policy-editor/value-types/all';
import {zodKeys} from '@/lib/utils/zod/zod-keys';
import {z} from 'zod';

/**
 * All field names of a tree node of the policy editor (used for react hook form)
 */
export const policyEditorNodeFormFields = zodKeys(
  policyEditorConstraintFormSchema,
);

export const policyEditorFormSchema = z.record(
  policyEditorConstraintFormSchema,
);

export type PolicyEditorFormValue = z.infer<typeof policyEditorFormSchema>;
