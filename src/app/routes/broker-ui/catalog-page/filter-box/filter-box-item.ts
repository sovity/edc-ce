import {CnfFilterItem} from '@sovity.de/broker-server-client';

export interface FilterBoxItem {
  type: 'ITEM' | 'NO_VALUE';
  id: string;
  label: string;
}

export function buildFilterBoxItems(items: CnfFilterItem[]): FilterBoxItem[] {
  return items.map(buildFilterBoxItem);
}

function buildFilterBoxItem(item: CnfFilterItem): FilterBoxItem {
  return {
    type: item.id === '' ? 'NO_VALUE' : 'ITEM',
    id: item.id,
    label: item.id === '' ? 'None' : item.title,
  };
}
