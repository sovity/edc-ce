/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

'use client';

import {api} from '@/lib/api/client';
import {useCancellablePromise} from '@/lib/hooks/use-cancellable-promise';
import {useTranslations} from 'next-intl';
import {useEffect} from 'react';
import {type UseFormReturn} from 'react-hook-form';
import {type DataOfferFormMode} from './data-offer-form-mode';

export const useAsyncIdValidation = ({
  form,
  assetIdFieldName,
  assetId,
  formMode,
}: {
  form: UseFormReturn<any>;
  assetIdFieldName: string;
  assetId: string;
  formMode: DataOfferFormMode;
}) => {
  const publishingMode = form.watch('publishing.mode') as string;
  const t = useTranslations();
  const withCancellation = useCancellablePromise();

  useEffect(() => {
    if (formMode === 'EDIT') {
      form.clearErrors(assetIdFieldName);
    } else {
      if (formMode === 'CREATE' && publishingMode === 'DONT_PUBLISH') {
        withCancellation(api.uiApi.isAssetIdAvailable({assetId}))
          .then((response) => {
            if (response.available) {
              form.clearErrors(assetIdFieldName);
            } else {
              form.setError(assetIdFieldName, {
                type: 'manual',
                message: t('General.Validators.assetIdTaken'),
              });
            }
          })
          .catch((error) => {
            console.error(t('General.Validators.errorCheckingAssetId'), error);
          });
      } else if (
        formMode === 'CREATE' &&
        publishingMode === 'PUBLISH_UNRESTRICTED'
      ) {
        withCancellation(
          Promise.all([
            api.uiApi.isAssetIdAvailable({assetId}),
            api.uiApi.isContractDefinitionIdAvailable({
              contractDefinitionId: assetId,
            }),
          ]),
        )
          .then(([assetIdResponse, contractDefinitionIdResponse]) => {
            if (
              assetIdResponse.available &&
              contractDefinitionIdResponse.available
            ) {
              form.clearErrors(assetIdFieldName);
            } else {
              form.setError(assetIdFieldName, {
                type: 'manual',
                message: t('General.Validators.dataOfferIdTaken'),
              });
            }
          })
          .catch((error) => {
            console.error(
              t('General.Validators.errorCheckingDataOfferId'),
              error,
            );
          });
      } else if (
        formMode === 'CREATE' &&
        publishingMode === 'PUBLISH_RESTRICTED'
      ) {
        withCancellation(
          Promise.all([
            api.uiApi.isAssetIdAvailable({assetId}),
            api.uiApi.isContractDefinitionIdAvailable({
              contractDefinitionId: assetId,
            }),
            api.uiApi.isPolicyIdAvailable({policyId: assetId}),
          ]),
        )
          .then(
            ([
              assetIdResponse,
              contractDefinitionIdResponse,
              policyIdResponce,
            ]) => {
              if (
                assetIdResponse.available &&
                contractDefinitionIdResponse.available &&
                policyIdResponce.available
              ) {
                form.clearErrors(assetIdFieldName);
              } else {
                form.setError(assetIdFieldName, {
                  type: 'manual',
                  message: t('General.Validators.dataOfferIdTaken'),
                });
              }
            },
          )
          .catch((error) => {
            console.error(
              t('General.Validators.errorCheckingDataOfferId'),
              error,
            );
          });
      }
    }
  }, [
    assetId,
    assetIdFieldName,
    form,
    formMode,
    publishingMode,
    t,
    withCancellation,
  ]);
};
