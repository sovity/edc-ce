import {DOCUMENT} from '@angular/common';
import {Component, Inject, OnInit} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../../../core/config/app-config';
import {LocationHistoryUtils} from './location-history-utils';

@Component({
  selector: 'logout',
  template: ``,
})
export class LogoutPageComponent implements OnInit {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    @Inject(DOCUMENT) private document: Document,
    private locationHistoryUtils: LocationHistoryUtils,
  ) {}

  ngOnInit(): void {
    // Prevent back button hijacking from /logout in history
    this.locationHistoryUtils.replaceStateWithPreviousUrl({
      skipUrlsStartingWith: '/logout',
    });
    this.document.location.href = this.config.logoutUrl!;
  }
}
