/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, HostBinding} from '@angular/core';

@Component({
  selector: 'app-page-not-found-page',
  templateUrl: './page-not-found-page.component.html',
})
export class PageNotFoundPageComponent {
  @HostBinding('class.container')
  @HostBinding('class.flex')
  @HostBinding('class.items-center')
  @HostBinding('class.min-h-screen')
  @HostBinding('class.px-6')
  @HostBinding('class.py-12')
  @HostBinding('class.box-border')
  cls = true;
}
