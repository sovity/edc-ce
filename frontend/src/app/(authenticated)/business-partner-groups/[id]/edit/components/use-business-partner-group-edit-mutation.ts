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
import {useMutation} from '@tanstack/react-query';
import {useTranslations} from 'next-intl';
import {type EditBusinessPartnerGroupForm} from './use-edit-business-partner-group-form';
import {useInvalidateId} from '@/lib/hooks/use-invalidate-id';

export const useBusinessPartnerGroupEditMutation = (groupId: string) => {
  const router = useRouter();
  const invalidateGroup = useInvalidateId(queryKeys.businessPartnerGroups);
  const t = useTranslations();
  const {toast} = useToast();

  return useMutation({
    mutationKey: ['editBusinessPartnerGroup'],
    mutationFn: async (
      formValue: EditBusinessPartnerGroupForm,
    ): Promise<IdResponseDto> => {
      return await api.uiApi.businessPartnerGroupEditSubmit({
        id: groupId,
        businessPartnerGroupEditSubmit: {
          businessPartnerNumbers: formValue.members,
        },
      });
    },
    onSuccess: async () => {
      toast({
        title: t('General.success'),
        description: t('Pages.BusinessPartnerGroupsEditForm.submitSuccess'),
      });

      await invalidateGroup(groupId);
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
