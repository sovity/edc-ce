/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {DATA_CATEGORY_SELECT_DATA} from './data-category-select-data';
import {DataCategorySelectItem} from './data-category-select-item';

@Component({
  selector: 'data-category-select',
  templateUrl: 'data-category-select.component.html',
})
export class DataCategorySelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<DataCategorySelectItem | null>;

  items = DATA_CATEGORY_SELECT_DATA;
}
