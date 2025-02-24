import {UiContractOffer} from '@sovity.de/edc-client';
import {PropertyGridField} from '../../../shared/common/property-grid/property-grid-field';

export type ContractOffer = UiContractOffer & {
  properties: PropertyGridField[];
};
