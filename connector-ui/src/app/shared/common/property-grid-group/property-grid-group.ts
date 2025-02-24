import {PropertyGridField} from '../property-grid/property-grid-field';

export interface PropertyGridGroup {
  groupLabel: string | null;
  properties: PropertyGridField[];
}
