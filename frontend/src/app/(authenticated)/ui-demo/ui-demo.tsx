/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InternalLink from '@/components/links/internal-link';
import {urls} from '@/lib/urls';
import * as React from 'react';
import ContractAgreementHeaderStack from '@/components/stacks/contract-agreement-header-stack';
import AssetHeaderStack from '@/components/stacks/asset-header-stack';

const UiDemoSection = ({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) => {
  return (
    <div className={'flex flex-col gap-8'}>
      <div className={'text-xl font-bold'}>{title}</div>
      {children}
    </div>
  );
};

const UiDemoVariant = ({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) => {
  return (
    <div className={'flex flex-col gap-8'}>
      <div className={''}>{title}</div>
      {children}
    </div>
  );
};

export default function UiDemo() {
  const contractAgreementProps = {
    contractAgreementId: '123',
    assetName: 'Example Data Offer',
    counterpartyParticipantId: 'BPNL1234XX.C1234XX',
    counterpartyOrganizationName: 'Other Organization Name',
  };

  return (
    <>
      <UiDemoSection title={'Page Titles'}>
        <UiDemoVariant title={'Ongoing Consuming Contract'}>
          <ContractAgreementHeaderStack
            direction={'CONSUMING'}
            terminationStatus={'ONGOING'}
            size={'page-title'}
            {...contractAgreementProps}>
            <InternalLink
              dataTestId={`btn-transfer-quick-access`}
              href={urls.contracts.transferPage('abc')}>
              Transfer
            </InternalLink>
          </ContractAgreementHeaderStack>
        </UiDemoVariant>
        <UiDemoVariant title={'Terminated Consuming Contract'}>
          <ContractAgreementHeaderStack
            direction={'CONSUMING'}
            terminationStatus={'TERMINATED'}
            size={'page-title'}
            {...contractAgreementProps}
          />
          <UiDemoVariant title={'Ongoing Providing Contract'}>
            <ContractAgreementHeaderStack
              direction={'PROVIDING'}
              terminationStatus={'ONGOING'}
              size={'page-title'}
              {...contractAgreementProps}
            />
          </UiDemoVariant>
        </UiDemoVariant>
        <UiDemoVariant title={'Terminated Providing Contract'}>
          <ContractAgreementHeaderStack
            direction={'PROVIDING'}
            terminationStatus={'TERMINATED'}
            size={'page-title'}
            {...contractAgreementProps}
          />
        </UiDemoVariant>
        <UiDemoVariant title={'Live Asset'}>
          <AssetHeaderStack
            assetId={'my-test-asset'}
            assetName={'My Test Asset'}
            dataSourceAvailability={'LIVE'}
            size={'page-title'}>
            <InternalLink
              dataTestId={`btn-transfer-quick-access`}
              variant={'outline'}
              href="#">
              Delete
            </InternalLink>
            <InternalLink dataTestId={`btn-transfer-quick-access`} href="#">
              Edit
            </InternalLink>
          </AssetHeaderStack>
        </UiDemoVariant>
        <UiDemoVariant title={'On Request Asset'}>
          <AssetHeaderStack
            assetId={'my-test-asset'}
            assetName={'My Test Asset'}
            dataSourceAvailability={'ON_REQUEST'}
            size={'page-title'}>
            <InternalLink
              dataTestId={`btn-transfer-quick-access`}
              variant={'outline'}
              href="#">
              Delete
            </InternalLink>
            <InternalLink dataTestId={`btn-transfer-quick-access`} href="#">
              Edit
            </InternalLink>
          </AssetHeaderStack>
        </UiDemoVariant>
      </UiDemoSection>
      <UiDemoSection title={'Table Cells'}>
        <UiDemoVariant title={'Ongoing Consuming Contract'}>
          <ContractAgreementHeaderStack
            direction={'CONSUMING'}
            terminationStatus={'ONGOING'}
            size={'table-cell'}
            {...contractAgreementProps}
          />
        </UiDemoVariant>
        <UiDemoVariant title={'Terminated Consuming Contract'}>
          <ContractAgreementHeaderStack
            direction={'CONSUMING'}
            terminationStatus={'TERMINATED'}
            size={'table-cell'}
            {...contractAgreementProps}
          />
        </UiDemoVariant>
        <UiDemoVariant title={'Ongoing Providing Contract'}>
          <ContractAgreementHeaderStack
            direction={'PROVIDING'}
            terminationStatus={'ONGOING'}
            size={'table-cell'}
            {...contractAgreementProps}
          />
        </UiDemoVariant>
        <UiDemoVariant title={'Terminated Providing Contract'}>
          <ContractAgreementHeaderStack
            direction={'PROVIDING'}
            terminationStatus={'TERMINATED'}
            size={'table-cell'}
            {...contractAgreementProps}
          />
        </UiDemoVariant>
        <UiDemoVariant title={'Live Asset'}>
          <AssetHeaderStack
            assetId={'my-test-asset'}
            assetName={'My Test Asset'}
            dataSourceAvailability={'LIVE'}
            size={'table-cell'}
          />
        </UiDemoVariant>
        <UiDemoVariant title={'On Request Asset'}>
          <AssetHeaderStack
            assetId={'my-test-asset'}
            assetName={'My Test Asset'}
            dataSourceAvailability={'ON_REQUEST'}
            size={'table-cell'}
          />
        </UiDemoVariant>
      </UiDemoSection>
    </>
  );
}
