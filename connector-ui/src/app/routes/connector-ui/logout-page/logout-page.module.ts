/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {LogoutPageComponent} from './logout-page.component';

@NgModule({
  imports: [
    // Angular
    CommonModule,
    RouterModule,
  ],
  declarations: [LogoutPageComponent],
  exports: [LogoutPageComponent],
})
export class LogoutPageModule {}
