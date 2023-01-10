import {FormControl, FormGroup, ɵFormGroupValue} from "@angular/forms";
import {DataAddressType} from "./data-address-type";
import {LanguageSelectItem} from "../language-select/language-select-item";
import {TransportModeSelectItem} from "../transport-mode-select/transport-mode-select-item";
import {DataSubcategorySelectItem} from "../data-subcategory-select/data-subcategory-select-item";
import {DataCategorySelectItem} from "../data-category-select/data-category-select-item";

/**
 * Form Value Type
 */
export type AssetEditorDialogFormValue = ɵFormGroupValue<AssetEditorDialogFormModel>;

/**
 * Form Group Template Type
 */
export interface AssetEditorDialogFormModel {
  metadata: FormGroup<AssetEditorDialogMetadataFormModel>;
  advanced: FormGroup<AssetEditorDialogAdvancedFormModel>;
  datasource: FormGroup<AssetEditorDialogDatasourceFormModel>;
}

export interface AssetEditorDialogMetadataFormModel {
  id: FormControl<string>;
  name: FormControl<string>;
  version: FormControl<string>;
  contenttype: FormControl<string>;
  description: FormControl<string>;
  keywords: FormControl<string[]>;
  language: FormControl<LanguageSelectItem | null>;
}

export interface AssetEditorDialogAdvancedFormModel {
  dataCategory: FormControl<DataCategorySelectItem | null>;
  dataSubcategory: FormControl<DataSubcategorySelectItem | null>;
  dataModel: FormControl<string>;
  geoReferenceMethod: FormControl<string>;
  transportMode: FormControl<TransportModeSelectItem | null>;
}

export interface AssetEditorDialogDatasourceFormModel {
  dataAddressType: FormControl<DataAddressType>;
  dataDestination: FormControl<string>;
  baseUrl: FormControl<string>;
  publisher: FormControl<string>;
  standardLicense: FormControl<string>;
  endpointDocumentation: FormControl<string>;
}
