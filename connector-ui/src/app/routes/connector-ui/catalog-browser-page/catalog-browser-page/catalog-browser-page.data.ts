import {DataOffer} from '../../../../core/services/models/data-offer';
import {Fetched} from '../../../../core/services/models/fetched';
import {MultiFetched} from '../../../../core/services/models/multi-fetched';

export interface CatalogBrowserPageData {
  requestTotals: MultiFetched<DataOffer[]>;
  requests: ContractOfferRequest[];
  filteredDataOffers: DataOffer[];
  numTotalContractOffers: number;
}

export function emptyCatalogBrowserPageData(): CatalogBrowserPageData {
  return {
    requests: [],
    requestTotals: MultiFetched.empty(),
    filteredDataOffers: [],
    numTotalContractOffers: 0,
  };
}

export interface ContractOfferRequest {
  url: string;
  isPresetUrl: boolean;
  data: Fetched<DataOffer[]>;
}
