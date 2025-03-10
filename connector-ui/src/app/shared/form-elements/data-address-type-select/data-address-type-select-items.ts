/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {ActiveFeatureSet} from 'src/app/core/config/active-feature-set';
import {DataAddressTypeSelectItem} from './data-address-type-select-item';
import {DataAddressTypeSelectMode} from './data-address-type-select-mode';

export const dataAddressTypeSelectItems = (
  type: DataAddressTypeSelectMode,
  activeFeatureSet: ActiveFeatureSet,
): DataAddressTypeSelectItem[] => {
  const items: DataAddressTypeSelectItem[] = [];

  if (type.startsWith('Datasource') && activeFeatureSet.hasMdsFields()) {
    items.push({
      id: 'On-Request',
      label: '"On Request" Data Offer',
    });
  }

  items.push({
    id: 'Http',
    label: 'REST-API Endpoint',
  });

  if (type === 'Datasource-Create') {
    items.push({
      id: 'Custom-Data-Address-Json',
      label: `Custom Datasource Config (JSON)`,
    });
  }

  if (type === 'Datasink') {
    items.push(
      {
        id: 'Custom-Data-Address-Json',
        label: `Custom Datasink Config (JSON) for Transfer Type 'HttpData-PUSH'`,
      },
      {
        id: 'Custom-Transfer-Process-Request',
        label: 'Custom Transfer Process Request (JSON)',
      },
    );
  }

  return items;
};
