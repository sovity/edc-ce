import {DOCUMENT, Location} from '@angular/common';
import {Component, Inject, OnInit} from '@angular/core';
import {AppConfigService} from '../../../app/config/app-config.service';
import {LocationHistoryUtils} from './location-history-utils';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.scss'],
})
export class LogoutComponent implements OnInit {
  constructor(
    private locationHistoryUtils: LocationHistoryUtils,
    private location: Location,
    private appConfigService: AppConfigService,
    @Inject(DOCUMENT) private document: Document,
  ) {}

  ngOnInit(): void {
    // Prevent back button hijacking from /logout in history
    this.locationHistoryUtils.replaceStateWithPreviousUrl({
      skipUrlsStartingWith: '/logout',
    });
    this.document.location.href = this.appConfigService.config.logoutUrl!;
  }
}
