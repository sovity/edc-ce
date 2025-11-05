/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {policyEditorFormSchema} from '@/components/policy-editor/editor/policy-editor-form-schema';
import {dataOfferTypeSchema} from '@/app/(authenticated)/data-offers/create/components/data-offer-type-schema';
import {type DataOfferPublishType} from '@sovity.de/edc-client';
import {z} from 'zod';
import {allowedIdRegex, invalidIdError} from '@/lib/utils/id-utils';
import {validateAssetId} from './use-async-id-validation';
import {type DataOfferFormMode} from './data-offer-form-mode';
import {type useTranslations} from 'next-intl';

const debounceValidateAssetId = validateAssetId();

const dataOfferBasicFieldsSchema = z.object({
  assetId: z.string().min(1).max(128).regex(allowedIdRegex, {
    message: invalidIdError,
  }),
  title: z.string().min(1).max(128),
  description: z.string().optional(),
  keywords: z.array(z.string()).optional(),
});

const dataOfferAdvancedFieldsSchema = z.discriminatedUnion(
  'showAdvancedFields',
  [
    z.object({
      showAdvancedFields: z.literal(false),
    }),
    z.object({
      showAdvancedFields: z.literal(true),
      version: z.string().optional(),
      contentType: z.string().optional(),
      language: z.string().optional(),
      publisher: z.string().url().optional(),
      standardLicense: z.string().url().optional(),
      endpointDocumentation: z.string().url().optional(),

      dataModel: z.string().optional(),
      sovereignLegalName: z.string().optional(),
      dataSampleUrls: z
        .array(
          z.object({
            value: z.string().url(),
          }),
        )
        .optional(),
      referenceFileUrls: z
        .array(
          z.object({
            value: z.string().url(),
          }),
        )
        .optional(),
      referenceFilesDescription: z.string().optional(),
      conditionsForUse: z.string().optional(),
      dataUpdateFrequency: z.string().optional(),
      temporalCoverage: z
        .object({
          startDate: z.date().optional(),
          endDate: z.date().optional(),
        })
        .optional(),
    }),
  ],
);

const dataOfferSphinxFieldsSchema = z.object({
  patientCount: z.string().optional(),
  birthYearMin: z.string().optional(),
  birthYearMax: z.string().optional(),
  administrativeGender: z.string().optional(),
  bodyHeightMin: z.string().optional(),
  bodyHeightMax: z.string().optional(),
  diagnosisPrimary: z.string().optional(),
  diagnosisSecondary: z.string().optional(),
  encounterStart: z.string().optional(),
  encounterEnd: z.string().optional(),
  medicationCount: z.string().optional(),
  dosageCount: z.string().optional(),
  clinicalSpecialty: z.string().optional(),
})


export const dataOfferFormSchema = (
  formMode: DataOfferFormMode,
  t: ReturnType<typeof useTranslations>,
) =>
  z
    .object({
      type: dataOfferTypeSchema,
      general: dataOfferBasicFieldsSchema,
      advanced: dataOfferAdvancedFieldsSchema,
      sphinxFields: dataOfferSphinxFieldsSchema,
      publishing: z.discriminatedUnion('mode', [
        z.object({
          mode: z.literal(
            'PUBLISH_UNRESTRICTED' satisfies DataOfferPublishType,
          ),
        }),
        z.object({
          mode: z.literal('PUBLISH_RESTRICTED' satisfies DataOfferPublishType),
          policy: policyEditorFormSchema,
        }),
        z.object({
          mode: z.literal('DONT_PUBLISH' satisfies DataOfferPublishType),
        }),
      ]),
    })
    .refine(
      (formData) =>
        debounceValidateAssetId(
          formData.general.assetId,
          formMode,
          formData.publishing.mode,
        ),
      {
        message: t('General.Validators.assetIdTaken'),
        path: ['general', 'assetId'],
      },
    );

export type DataOfferCreateFormModel = z.infer<
  ReturnType<typeof dataOfferFormSchema>
>;
