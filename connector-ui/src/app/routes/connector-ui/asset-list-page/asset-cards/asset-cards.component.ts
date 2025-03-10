/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {UiAssetMapped} from '../../../../core/services/models/ui-asset-mapped';

@Component({
  selector: 'asset-cards',
  templateUrl: './asset-cards.component.html',
})
export class AssetCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  assets: UiAssetMapped[] = [];

  @Output()
  assetClick = new EventEmitter<UiAssetMapped>();
}
