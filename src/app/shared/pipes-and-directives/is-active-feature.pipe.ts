import {Pipe, PipeTransform} from '@angular/core';
import {ActiveFeatureSet} from '../../core/config/active-feature-set';
import {EdcUiFeature} from '../../core/config/profiles/edc-ui-feature';

/**
 * Easily check for active features in angular templates.
 */
@Pipe({name: 'isActiveFeature'})
export class IsActiveFeaturePipe implements PipeTransform {
  constructor(private activeFeatureSet: ActiveFeatureSet) {}

  transform(feature: EdcUiFeature): boolean {
    return this.activeFeatureSet.has(feature);
  }
}
