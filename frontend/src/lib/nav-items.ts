/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {env} from '@/env';
import {
  DocumentTextIcon,
  ServerStackIcon,
  ShieldCheckIcon,
} from '@heroicons/react/24/outline';
import {
  FileClockIcon,
  FilePlusIcon,
  FileStackIcon,
  Rows3Icon,
  SquareLibraryIcon,
} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {urls} from './urls';
import {type SvgIcon} from './utils/svg-icon';

export interface NavItem {
  name: string;
  href: string;
  icon: SvgIcon;
}

export interface NavItemGroup {
  title: string;
  items: NavItem[];
}

export const useNavItems = (): NavItemGroup[] => {
  const t = useTranslations();

  return [
    {
      title: '',
      items: [
        {
          name: t('Pages.Dashboard.title'),
          href: urls.rootPage(),
          icon: ServerStackIcon,
        },
        {
          name: t('Pages.ContractList.title'),
          href: urls.contracts.listPage(),
          icon: SquareLibraryIcon,
        },
        {
          name: t('Pages.TransferHistory.title'),
          href: urls.transferHistory(),
          icon: FileClockIcon,
        },
      ],
    },
    {
      title: t('General.consume'),
      items: [
        {
          name: t('Pages.CatalogBrowser.title'),
          href: urls.catalog.browserPage(),
          icon: FileStackIcon,
        },
      ],
    },
    {
      title: t('General.provide'),
      items: [
        {
          name: t('Pages.DataOfferCreate.title'),
          href: urls.dataOffers.createPage(),
          icon: FilePlusIcon,
        },
        {
          name: t('Pages.DataOfferList.title'),
          href: urls.dataOffers.listPage(),
          icon: Rows3Icon,
        },
        {
          name: t('Pages.PolicyList.title'),
          href: urls.policies.listPage(),
          icon: ShieldCheckIcon,
        },
        {
          name: t('General.assets'),
          href: urls.assets.listPage(),
          icon: FileStackIcon,
        },
      ],
    },
    ...(env.NEXT_PUBLIC_DEVTOOLS_ENABLED
      ? [
          {
            title: 'Dev Tools',
            items: [
              {
                name: 'UI / UX Test Page',
                href: '/ui-demo',
                icon: DocumentTextIcon,
              },
            ],
          },
        ]
      : []),
  ];
};
