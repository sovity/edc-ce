import {Injectable} from '@angular/core';
import {AppConfigService} from './app-config.service';
import {EdcUiFeature} from './edc-ui-feature';

@Injectable({providedIn: 'root'})
export class ActiveFeatureSet {
  constructor(private appConfigService: AppConfigService) {}

  hasMdsFields(): boolean {
    return this.has('mds-fields');
  }

  has(feature: EdcUiFeature): boolean {
    return this.appConfigService.config.features.has(feature);
  }
}
