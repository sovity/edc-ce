import {DataAddressTypeSelectItem} from './data-address-type-select-item';
import {DataAddressTypeSelectMode} from './data-address-type-select-mode';

export const dataAddressTypeSelectItems = (
  type: DataAddressTypeSelectMode,
): DataAddressTypeSelectItem[] => [
  {
    id: 'Http',
    label: 'REST-API Endpoint',
  },
  {
    id: 'Custom-Data-Address-Json',
    label: `Custom ${type} Config (JSON)`,
  },
];
