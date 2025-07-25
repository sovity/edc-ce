/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import {useRouter} from 'next/navigation';
import {useToast} from '@/components/ui/use-toast';
import {api} from '@/lib/api/client';
import {queryKeys} from '@/lib/queryKeys';
import {urls} from '@/lib/urls';
import {type IdResponseDto} from '@sovity.de/edc-client';
import {useMutation, useQueryClient} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';
import {type CreateBusinessPartnerGroupForm} from './use-create-business-partner-group-form';

export const useBusinessPartnerGroupCreateMutation = () => {
  const router = useRouter();
  const queryClient = useQueryClient();
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationKey: ['createBusinessPartnerGroup'],
    mutationFn: async (
      formValue: CreateBusinessPartnerGroupForm,
    ): Promise<IdResponseDto> => {
      return await api.uiApi.businessPartnerGroupCreateSubmit({
        businessPartnerGroupCreateSubmit: {
          groupId: formValue.groupId,
          businessPartnerNumbers: formValue.members,
        },
      });
    },
    onSuccess: async () => {
      toast({
        title: t('General.success'),
        description: t('Pages.BusinessPartnerGroupsCreateForm.submitSuccess'),
      });

      await queryClient.invalidateQueries(
        queryKeys.businessPartnerGroups.listPage(),
      );
      router.push(urls.businessPartnerGroups.listPage());
    },
    onError: (error: {message: string}) => {
      toast({
        title: `❌ ${t('General.error')}`,
        description: error.message,
      });
    },
  });
};
