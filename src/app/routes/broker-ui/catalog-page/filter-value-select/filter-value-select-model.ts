import {FilterValueSelectItem} from './filter-value-select-item';

export interface FilterValueSelectModel {
  id: string;
  title: string;
  selectedItems: FilterValueSelectItem[];
  availableItems: FilterValueSelectItem[];
  searchText: string;
}
