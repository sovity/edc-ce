import {FormControl} from '@angular/forms';
import {LanguageSelectItem} from '../../language-select/language-select-item';

/**
 * Form Model for AssetEditorDialog > Metadata
 */
export interface AssetMetadataFormModel {
  id: FormControl<string>;
  name: FormControl<string>;
  version: FormControl<string>;
  contentType: FormControl<string>;
  description: FormControl<string>;
  keywords: FormControl<string[]>;
  language: FormControl<LanguageSelectItem | null>;
  publisher: FormControl<string>;
  standardLicense: FormControl<string>;
  endpointDocumentation: FormControl<string>;
}
