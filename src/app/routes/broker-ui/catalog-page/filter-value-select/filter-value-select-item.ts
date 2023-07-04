import {CnfFilterItem} from '@sovity.de/broker-server-client';

export interface FilterValueSelectItem {
  type: 'ITEM' | 'NO_VALUE';
  id: string;
  label: string;
}

export function mapCnfFilterItems(
  items: CnfFilterItem[],
): FilterValueSelectItem[] {
  return items.map((item) => ({
    type: item.id === '' ? 'NO_VALUE' : 'ITEM',
    id: item.id,
    label: item.id === '' ? 'None' : item.title,
  }));
}
