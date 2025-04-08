/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import FormGroup from '@/components/form/form-group';
import SelectField from '@/components/form/select-field';
import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {InitiateTransferCustomForm} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-custom-form';
import {useInitiateTransferForm} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-hook';
import {
  type InitiateTransferFormValue,
  type InitiateTransferType,
} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-form-schema';
import {InitiateTransferHttpForm} from '@/app/(authenticated)/contracts/[contractId]/transfer/components/initiate-transfer-http-form';
import {useContractTransferMutation} from '@/app/(authenticated)/contracts/[contractId]/transfer/use-contract-transfer-mutation';
import {type ContractAgreementCard} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';
import ContractAgreementHeaderStack from '@/components/stacks/contract-agreement-header-stack';
import InternalLink from '@/components/links/internal-link';
import {urls} from '@/lib/urls';

export const InitiateTransferForm = ({
  contractAgreement,
}: {
  contractAgreement: ContractAgreementCard;
}) => {
  const {form} = useInitiateTransferForm();
  const mutation = useContractTransferMutation();
  const t = useTranslations();

  async function onSubmit(values: InitiateTransferFormValue) {
    await mutation.mutateAsync({
      formValue: values,
      contractAgreement,
    });
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <ContractAgreementHeaderStack
          size={'page-title'}
          contractAgreementId={contractAgreement.contractAgreementId}
          counterpartyParticipantId={contractAgreement.counterPartyId}
          terminationStatus={contractAgreement.terminationStatus}
          direction={contractAgreement.direction}
          assetName={
            contractAgreement.asset.title
          }></ContractAgreementHeaderStack>

        <FormGroup title={'Data Sink'} subTitle={'Where to transfer the data?'}>
          {/* Data Address Type */}
          <SelectField
            control={form.control}
            name={'transferType'}
            label={t('Pages.DataOfferCreate.type')}
            items={[
              {
                id: 'CUSTOM_JSON' satisfies InitiateTransferType,
                label: t('Pages.DataOfferCreate.dataSourceTypeCustom'),
              },
              {
                id: 'HTTP' satisfies InitiateTransferType,
                label: t('Pages.DataOfferCreate.dataSourceTypeHttp'),
              },
            ]}
            placeholder={''}
          />

          <InitiateTransferCustomForm form={form} />

          <InitiateTransferHttpForm form={form} />
        </FormGroup>

        <div className="flex justify-between gap-2">
          <InternalLink
            variant={'outline'}
            dataTestId={'btn-back'}
            href={urls.contracts.detailPage(
              contractAgreement.contractAgreementId,
            )}>
            {t('General.back')}
          </InternalLink>
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {t('General.initiateTransfer')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
