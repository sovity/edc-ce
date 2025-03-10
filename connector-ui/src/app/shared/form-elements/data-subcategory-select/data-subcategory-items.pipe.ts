/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Pipe, PipeTransform} from '@angular/core';
import {DataCategorySelectItem} from '../data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from './data-subcategory-select-item';
import {DataSubcategorySelectItemService} from './data-subcategory-select-item.service';

@Pipe({
  name: 'dataSubcategoryItems',
})
export class DataSubcategoryItemsPipe implements PipeTransform {
  constructor(private items: DataSubcategorySelectItemService) {}

  transform(
    dataCategory: DataCategorySelectItem | null,
  ): DataSubcategorySelectItem[] {
    return this.items.findByDataCategory(dataCategory);
  }
}
