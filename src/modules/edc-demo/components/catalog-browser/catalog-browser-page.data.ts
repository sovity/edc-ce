import {ContractOffer} from '../../models/contract-offer';
import {Fetched} from '../../models/fetched';
import {MultiFetched} from '../../models/multi-fetched';

export interface CatalogBrowserPageData {
  requestTotals: MultiFetched<ContractOffer[]>;
  requests: ContractOfferRequest[];
  filteredContractOffers: ContractOffer[];
  numTotalContractOffers: number;
}

export function emptyCatalogBrowserPageData(): CatalogBrowserPageData {
  return {
    requests: [],
    requestTotals: MultiFetched.empty(),
    filteredContractOffers: [],
    numTotalContractOffers: 0,
  };
}

export interface ContractOfferRequest {
  url: string;
  isPresetUrl: boolean;
  data: Fetched<ContractOffer[]>;
}
