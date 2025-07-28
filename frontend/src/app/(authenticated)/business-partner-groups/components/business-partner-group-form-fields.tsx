/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import InputField from '@/components/form/input-field';
import {useTranslations} from 'next-intl';
import {type EditBusinessPartnerGroupForm} from '../[id]/edit/components/use-edit-business-partner-group-form';
import type {Control} from 'react-hook-form';
import {type CreateBusinessPartnerGroupForm} from '../create/components/use-create-business-partner-group-form';
import ArrayComboboxField from '@/components/form/array-combobox-field';
import FormGroup from '@/components/form/form-group';

export const BusinessPartnerGroupFormFields = <
  T extends CreateBusinessPartnerGroupForm | EditBusinessPartnerGroupForm,
>({
  createForm,
  control,
}: {
  createForm?: boolean;
  control: Control<T>;
}) => {
  const t = useTranslations();
  return (
    <>
      <FormGroup
        title={t('Pages.BusinessPartnerGroupsForm.generalInformationSection')}
        subTitle={t(
          'Pages.BusinessPartnerGroupsForm.generalInformationSectionDescription',
        )}>
        <InputField
          disabled={!createForm}
          control={control}
          label={t('Pages.BusinessPartnerGroupsForm.groupId')}
          placeholder={'group-id'}
          name="groupId"
          isRequired
        />
      </FormGroup>
      <FormGroup
        title={t('Pages.BusinessPartnerGroupsForm.membersSection')}
        subTitle={t(
          'Pages.BusinessPartnerGroupsForm.membersSectionDescription',
        )}>
        <ArrayComboboxField
          control={control}
          name="members"
          label={t('Pages.BusinessPartnerGroupsForm.groupMembers')}
          searchEmptyMessage={t(
            'Pages.BusinessPartnerGroupsForm.searchEmptyMessage',
          )}
          searchPlaceholder={t(
            'Pages.BusinessPartnerGroupsForm.searchPlaceholder',
          )}
          addValueLabel={t('Pages.BusinessPartnerGroupsForm.addBPN')}
          selectPlaceholder="BPNL1234XX.C1237XX, BPNL1234XX.C1235XX"
        />
      </FormGroup>
    </>
  );
};
