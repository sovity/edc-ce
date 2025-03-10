/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {associateBy} from 'src/app/core/utils/map-utils';
import {TRANSPORT_MODE_SELECT_DATA} from './transport-mode-select-data';
import {TransportModeSelectItem} from './transport-mode-select-item';

/**
 * Access list of available TransportModeSelectItems
 */
@Injectable({providedIn: 'root'})
export class TransportModeSelectItemService {
  itemsById = associateBy(TRANSPORT_MODE_SELECT_DATA, (it) => it.id);

  /**
   * Find TransportModeSelectItem by id
   * @param id language select item id
   */
  findById(id: string): TransportModeSelectItem {
    const item = this.itemsById.get(id);
    if (item != null) {
      return item;
    }
    return {
      id,
      label: id,
    };
  }
}
