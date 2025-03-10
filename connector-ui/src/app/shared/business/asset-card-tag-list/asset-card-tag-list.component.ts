/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding, Input} from '@angular/core';

@Component({
  selector: 'asset-card-tag-list',
  templateUrl: './asset-card-tag-list.component.html',
})
export class AssetCardTagListComponent {
  @HostBinding('class.block') cls = true;
  @Input() numberOfKeywordsDisplayed: number = 3;
  @Input() keywords: string[] | undefined;
  @Input() version: string | undefined;
}
