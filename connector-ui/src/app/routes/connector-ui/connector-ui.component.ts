import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {Component, Inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {NavItemGroup} from 'src/app/core/services/models/nav-item-group';
import {NavItemsBuilder} from 'src/app/core/services/nav-items-builder';
import {isLanguageSupported} from 'src/app/core/utils/i18n-utils';
import {LocalStoredValue} from 'src/app/core/utils/local-stored-value';
import {APP_CONFIG, AppConfig} from '../../core/config/app-config';
import {LoginPollingService} from '../../core/services/login-polling.service';

@Component({
  selector: 'connector-ui-page-layout',
  templateUrl: './connector-ui.component.html',
  styleUrls: ['./connector-ui.component.scss'],
})
export class ConnectorUiComponent implements OnInit {
  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay(),
    );

  navItemGroups: NavItemGroup[] = [];

  selectedLanguage = new LocalStoredValue<string>(
    'en',
    'selectedLanguage',
    isLanguageSupported,
  );

  constructor(
    @Inject(APP_CONFIG) public config: AppConfig,
    public titleService: Title,
    private breakpointObserver: BreakpointObserver,
    private loginPollingService: LoginPollingService,
    private navItemsBuilder: NavItemsBuilder,
    private translateService: TranslateService,
  ) {
    this.navItemGroups = this.navItemsBuilder.buildNavItemGroups();
    this.translateService.setDefaultLang('en');
    this.translateService.use(this.selectedLanguage.value);
  }

  ngOnInit() {
    this.startLoginPolling();
  }

  private startLoginPolling() {
    this.loginPollingService.startPolling();
  }
}
