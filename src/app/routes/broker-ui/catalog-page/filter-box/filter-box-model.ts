import {CnfFilterAttribute} from '@sovity.de/broker-server-client';
import {FilterBoxItem, buildFilterBoxItems} from './filter-box-item';

/**
 * State of a single Filter
 */
export interface FilterBoxModel {
  id: string;
  title: string;
  selectedItems: FilterBoxItem[];
  availableItems: FilterBoxItem[];
  searchText: string;
}

export function buildFilterBoxModelWithNewData(
  fetched: CnfFilterAttribute,
  old: FilterBoxModel | null,
): FilterBoxModel {
  const availableItems = buildFilterBoxItems(fetched.values);
  return {
    id: fetched.id,
    title: fetched.title,
    availableItems,
    searchText: old?.searchText ?? '',
    selectedItems: old?.selectedItems ?? [],
  };
}
