/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {DATA_CATEGORY_SELECT_DATA} from './data-category-select-data';
import {DataCategorySelectItem} from './data-category-select-item';

/**
 * Access list of available DataCategorySelectItems
 */
@Injectable({providedIn: 'root'})
export class DataCategorySelectItemService {
  itemsById: Map<string, DataCategorySelectItem>;

  constructor() {
    this.itemsById = this.buildItemsMap();
  }

  /**
   * Find DataCategorySelectItem by id
   * @param id language select item id
   */
  findById(id: string): DataCategorySelectItem {
    const item = this.itemsById.get(id);
    if (item != null) {
      return item;
    }
    return {
      id,
      label: id,
    };
  }

  private buildItemsMap(): Map<string, DataCategorySelectItem> {
    return new Map(DATA_CATEGORY_SELECT_DATA.map((it) => [it.id, it]));
  }
}
