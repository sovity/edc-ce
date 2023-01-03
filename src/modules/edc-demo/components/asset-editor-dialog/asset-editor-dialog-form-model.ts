import {FormControl, FormGroup, ɵFormGroupValue} from "@angular/forms";
import {DataAddressType} from "./data-address-type";
import {LanguageSelectItem} from "../language-select/language-select-item";
import {PaymentModalitySelectItem} from "../payment-modality-select/payment-modality-select-item";

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
  keywords: FormControl<string>;
  language: FormControl<LanguageSelectItem>;
  paymentModality: FormControl<PaymentModalitySelectItem>;
}

export interface AssetEditorDialogAdvancedFormModel {
  dataCategory: FormControl<string>;
  dataModel: FormControl<string>;
  geoReferenceMethod: FormControl<string>;
  transportMode: FormControl<string>;
}

export interface AssetEditorDialogDatasourceFormModel {
  dataAddressType: FormControl<DataAddressType>;
  dataDestination: FormControl<string>;
  baseUrl: FormControl<string>;
  publisher: FormControl<string>;
  standardLicense: FormControl<string>;
  endpointDocumentation: FormControl<string>;
}

