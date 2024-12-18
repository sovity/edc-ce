import {EdcUiFeature} from '../../config/profiles/edc-ui-feature';

export interface NavItem {
  path: string;
  title: string;
  icon: string;
  requiresFeature?: EdcUiFeature;
}
