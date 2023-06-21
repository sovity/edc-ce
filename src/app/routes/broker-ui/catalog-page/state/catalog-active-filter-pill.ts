import {FilterValueSelectItem} from '../filter-value-select/filter-value-select-item';

export interface CatalogActiveFilterPill {
  type: 'SEARCH_TEXT' | 'SELECTED_FILTER_ITEM';
  label: string;
  value: string;

  selectedFilterId?: string;
  selectedFilterItem?: FilterValueSelectItem;
}
