import {AssetRequestBuilder} from 'src/app/core/services/asset-request-builder';
import {policyFormRequiredViewProviders} from '../policy-editor/editor/policy-form-required-providers';
import {AssetAdvancedFormBuilder} from './form/asset-advanced-form-builder';
import {AssetDatasourceFormBuilder} from './form/asset-datasource-form-builder';
import {AssetGeneralFormBuilder} from './form/asset-general-form-builder';
import {EditAssetForm} from './form/edit-asset-form';
import {EditAssetFormInitializer} from './form/edit-asset-form-initializer';

export const editAssetFormRequiredViewProviders = [
  EditAssetFormInitializer,
  AssetRequestBuilder,
  EditAssetForm,
  AssetGeneralFormBuilder,
  AssetDatasourceFormBuilder,
  AssetAdvancedFormBuilder,
  ...policyFormRequiredViewProviders,
];
