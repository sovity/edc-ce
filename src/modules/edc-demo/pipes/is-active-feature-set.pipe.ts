import {Pipe, PipeTransform} from "@angular/core";
import {EdcUiFeatureSet} from "../../../environments/edc-ui-feature-set";
import {environment} from "../../../environments/environment";

/**
 * Checks whether given feature set is currently active
 * @param featureSet e.g. mds etc.
 */
export function isFeatureSetActive(featureSet: EdcUiFeatureSet): boolean {
  return environment.activeFeatureSet === featureSet;
}

/**
 * Use this in angular templates to check active feature set (e.g. "mds" etc.)
 */
@Pipe({name: 'isActiveFeatureSet'})
export class IsActiveFeatureSetPipe implements PipeTransform {
  transform(featureSet: EdcUiFeatureSet): boolean {
    return isFeatureSetActive(featureSet)
  }
}
