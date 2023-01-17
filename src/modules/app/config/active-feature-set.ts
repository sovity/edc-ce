import {Injectable} from '@angular/core';
import {AppConfigService} from './app-config.service';
import {EdcUiFeatureSet} from './edc-ui-feature-set';

@Injectable({providedIn: 'root'})
export class ActiveFeatureSet {
  constructor(private appConfigService: AppConfigService) {}

  isMds(): boolean {
    return this.is('mds');
  }

  is(featureSet: EdcUiFeatureSet): boolean {
    return this.appConfigService.config.activeFeatureSet === featureSet;
  }
}
