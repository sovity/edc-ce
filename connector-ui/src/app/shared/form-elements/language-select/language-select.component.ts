/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {LanguageSelectItem} from './language-select-item';
import {LanguageSelectItemService} from './language-select-item.service';

@Component({
  selector: 'language-select',
  templateUrl: 'language-select.component.html',
})
export class LanguageSelectComponent {
  @Input()
  label: string | null = null;

  @Input()
  control!: FormControl<LanguageSelectItem | null>;

  constructor(public items: LanguageSelectItemService) {}
}
