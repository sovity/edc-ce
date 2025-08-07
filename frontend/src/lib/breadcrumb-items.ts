/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {useTranslations} from 'next-intl';
import {urls} from './urls';

export const useBreadcrumbItems = () => {
  const t = useTranslations();

  return {
    dashboard: () => ({
      label: t('Pages.Dashboard.title'),
      href: urls.rootPage(),
    }),
    assets: {
      listPage: () => ({
        label: t('General.assets'),
        href: urls.assets.listPage(),
      }),
      detailPage: (assetId: string, assetName?: string) => ({
        label: assetName ?? assetId,
        href: urls.assets.detailPage(assetId),
      }),
      createPage: () => ({
        label: t('Pages.AssetCreate.title'),
        href: urls.assets.createPage(),
      }),
      editPage: (assetId: string) => ({
        label: t('General.edit'),
        href: urls.assets.editPage(assetId),
      }),
    },
    vaultSecrets: {
      listPage: () => ({
        label: t('General.vaultSecrets'),
        href: urls.vaultSecrets.listPage(),
      }),
      createPage: () => ({
        label: t('General.create'),
        href: urls.vaultSecrets.createPage(),
      }),
      detailPage: (key: string) => ({
        label: key,
      }),
      editPage: (key: string) => ({
        label: t('General.edit'),
        href: urls.vaultSecrets.editPage(key),
      }),
    },
    businessPartnerGroups: {
      createPage: () => ({
        label: t('General.create'),
        href: urls.businessPartnerGroups.createPage(),
      }),
      listPage: () => ({
        label: t('General.businessPartnerGroups'),
        href: urls.businessPartnerGroups.listPage(),
      }),
      detailPage: (id: string) => ({
        label: id,
      }),
      editPage: (id: string) => ({
        label: t('General.edit'),
        href: urls.businessPartnerGroups.editPage(id),
      }),
    },
    contracts: {
      listPage: () => ({
        label: t('Pages.ContractList.title'),
        href: urls.contracts.listPage(),
      }),
      detailPage: (contractId: string) => ({
        label: contractId,
        href: urls.contracts.detailPage(contractId),
      }),
      transferPage: (contractId: string) => ({
        label: t('Pages.InitiateTransfer.title'),
        href: urls.contracts.transferPage(contractId),
      }),
    },
    policies: {
      listPage: () => ({
        label: t('Pages.PolicyList.title'),
        href: urls.policies.listPage(),
      }),
      createPage: () => ({
        label: t('Pages.PolicyCreate.title'),
        href: urls.policies.createPage(),
      }),
    },
    transferHistoryList: () => ({
      label: t('Pages.TransferHistory.title'),
      href: urls.transferHistory(),
    }),
    dataOffers: {
      listPage: () => ({
        label: t('Pages.DataOfferList.title'),
        href: urls.dataOffers.listPage(),
      }),
      publishPage: () => ({
        label: t('Pages.PublishDataOffer.title'),
        href: urls.dataOffers.publishPage(),
      }),
      createPage: () => ({
        label: t('Pages.DataOfferCreate.title'),
        href: urls.dataOffers.createPage(),
      }),
    },

    catalog: {
      home: () => ({
        label: t('Pages.CatalogBrowser.title'),
        href: urls.catalog.browserPage(),
      }),
      dataOfferList: (participantId: string, endpointUrl: string) => ({
        label: participantId,
        href: urls.catalog.listPage(participantId, endpointUrl),
      }),
      dataOfferDetail: (
        participantId: string,
        endpointUrl: string,
        assetId: string,
        assetName?: string,
      ) => ({
        label: assetName ?? assetId,
        href: urls.catalog.detailPage(participantId, endpointUrl, assetId),
      }),
    },
  };
};
