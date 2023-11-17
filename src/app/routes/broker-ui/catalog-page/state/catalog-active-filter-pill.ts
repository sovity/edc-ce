import {FilterBoxItem} from '../filter-box/filter-box-item';

export interface CatalogActiveFilterPill {
  type: 'SEARCH_TEXT' | 'SELECTED_FILTER_ITEM';
  label: string;
  value: string;

  selectedFilterId?: string;
  selectedFilterItem?: FilterBoxItem;
}
