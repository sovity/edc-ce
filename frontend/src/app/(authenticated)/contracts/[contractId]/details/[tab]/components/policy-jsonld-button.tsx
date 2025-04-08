/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ScrollableDialogButton from '@/components/scrollable-dialog/scrollable-dialog-button';
import {type UiPolicy} from '@/lib/api/client/generated';
import {useTranslations} from 'next-intl';

export interface ContractAgreementDetailsProps {
  dialogId: string;
  policy: UiPolicy;
}

export default function PolicyJsonLdButton(
  props: ContractAgreementDetailsProps,
) {
  const t = useTranslations();
  const {dialogId, policy} = props;

  return (
    <ScrollableDialogButton
      dialogId={dialogId}
      data={{
        title: t('Pages.CatalogDataOfferDetails.policyJsonLd'),
        content: (
          <div className="whitespace-pre-wrap font-mono text-sm">
            {JSON.stringify(JSON.parse(policy.policyJsonLd), null, 2)}
          </div>
        ),
      }}
      buttonText={t(
        'Pages.CatalogDataOfferDetails.showJsonLd',
      )}></ScrollableDialogButton>
  );
}
