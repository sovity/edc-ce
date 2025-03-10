/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {ContractAgreementCard} from '@sovity.de/edc-client';
import {AssetBuilder} from '../../../../core/services/asset-builder';
import {assetSearchTargets, search} from '../../../../core/utils/search-utils';
import {ContractAgreementCardMapped} from './contract-agreement-card-mapped';

@Injectable({providedIn: 'root'})
export class ContractAgreementCardMappedService {
  constructor(private assetBuilder: AssetBuilder) {}

  /**
   * Replace the asset with the parsed asset and add the other required fields of the UI model
   *
   * {@link ContractAgreementCardMapped}
   * @param contractAgreement {@link ContractAgreementCard}
   * @returns {@link ContractAgreementCardMapped}
   */
  buildContractAgreementCardMapped(
    contractAgreement: ContractAgreementCard,
  ): ContractAgreementCardMapped {
    const asset = this.assetBuilder.buildAsset(contractAgreement.asset);
    const isTerminated = contractAgreement.terminationStatus === 'TERMINATED';

    return {
      ...contractAgreement,
      asset,
      isInProgress: contractAgreement.transferProcesses.some(
        (it) => it.state.simplifiedState === 'RUNNING',
      ),
      isConsumingLimitsEnforced: false,
      isTerminated: isTerminated,
      searchTargets: [
        contractAgreement.contractAgreementId,
        contractAgreement.counterPartyId,
        contractAgreement.counterPartyAddress,
        ...assetSearchTargets(asset),
      ],
    };
  }

  filter(
    cards: ContractAgreementCardMapped[],
    searchText: string,
  ): ContractAgreementCardMapped[] {
    return search(cards, searchText, (card) => card.searchTargets);
  }

  withEnforcedLimits(
    maxConsumingContracts: number,
    agreements: ContractAgreementCardMapped[],
  ): ContractAgreementCardMapped[] {
    return agreements.map((it) => {
      if (it.isTerminated) {
        return it;
      }

      const modifiedAgreement = {
        ...it,
        isConsumingLimitsEnforced: true,
      };
      return modifiedAgreement;
    });
  }
}
