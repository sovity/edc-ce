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

    return {
      ...contractAgreement,
      asset,
      isInProgress: contractAgreement.transferProcesses.some(
        (it) => it.state.simplifiedState === 'RUNNING',
      ),
      isConsumingLimitsEnforced: false,
      statusText: '',
      statusTooltipText: '',
      canTransfer: true,
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
    return agreements.map((it, index) => ({
      ...it,
      isConsumingLimitsEnforced: true,
      statusText: index < maxConsumingContracts ? 'Active' : 'Inactive',
      statusTooltipText: this.getConsumingContractsInfoText(
        index,
        maxConsumingContracts,
      ),
      canTransfer: index < maxConsumingContracts,
    }));
  }

  private getConsumingContractsInfoText(
    index: number,
    maxConsumingContracts: number,
  ): string {
    if (index >= maxConsumingContracts) {
      return `This connector is licensed for a maximum number of ${maxConsumingContracts} consuming contract${
        maxConsumingContracts == 1 ? '' : 's'
      }. When negotiating new contracts, older contracts will be deactivated.`;
    } else {
      return '';
    }
  }
}
