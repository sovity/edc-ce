import {Component, Inject, OnInit} from '@angular/core';
import {APP_CONFIG, AppConfig} from './core/config/app-config';
import {FaviconService} from './core/services/favicon.service';
import {LoginPollingService} from './core/services/login-polling.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private loginPollingService: LoginPollingService,
    private faviconService: FaviconService,
  ) {}

  ngOnInit(): void {
    this.setThemeFromConfig();
    this.startLoginPolling();
  }

  private setThemeFromConfig() {
    window.document.body.classList.add(this.config.theme);
    this.faviconService.setFavicon(this.config.brandFaviconSrc);
  }

  private startLoginPolling() {
    this.loginPollingService.startPolling();
  }
}
