import {Pipe, PipeTransform} from '@angular/core';
import {ActiveFeatureSet} from '../../app/config/active-feature-set';
import {EdcUiFeatureSet} from '../../app/config/edc-ui-feature-set';

/**
 * Use this in angular templates to check active feature set (e.g. "mds" etc.)
 */
@Pipe({name: 'isActiveFeatureSet'})
export class IsActiveFeatureSetPipe implements PipeTransform {
  constructor(private activeFeatureSet: ActiveFeatureSet) {}

  transform(featureSet: EdcUiFeatureSet): boolean {
    return this.activeFeatureSet.is(featureSet);
  }
}
