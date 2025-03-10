/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Inject, OnInit} from '@angular/core';
import {APP_CONFIG, AppConfig} from './core/config/app-config';
import {FaviconService} from './core/services/favicon.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private faviconService: FaviconService,
  ) {}

  ngOnInit(): void {
    this.setThemeFromConfig();
  }

  private setThemeFromConfig() {
    window.document.body.classList.add(this.config.theme);
    this.faviconService.setFavicon(this.config.brandFaviconSrc);
  }
}
