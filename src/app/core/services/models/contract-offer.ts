import {UiContractOffer} from '@sovity.de/edc-client';
import {PropertyGridField} from '../../../component-library/property-grid/property-grid/property-grid-field';

export type ContractOffer = UiContractOffer & {
  properties: PropertyGridField[];
};
