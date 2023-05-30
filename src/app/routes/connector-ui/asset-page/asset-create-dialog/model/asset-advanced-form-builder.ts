import {Injectable} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataCategorySelectItem} from '../../data-category-select/data-category-select-item';
import {DataSubcategorySelectItem} from '../../data-subcategory-select/data-subcategory-select-item';
import {TransportModeSelectItem} from '../../transport-mode-select/transport-mode-select-item';
import {AssetAdvancedFormModel} from './asset-advanced-form-model';

@Injectable()
export class AssetAdvancedFormBuilder {
  constructor(private formBuilder: FormBuilder) {}

  buildFormGroup(): FormGroup<AssetAdvancedFormModel> {
    return this.formBuilder.nonNullable.group({
      dataModel: '',
      dataCategory: [
        null as DataCategorySelectItem | null,
        Validators.required,
      ],
      dataSubcategory: null as DataSubcategorySelectItem | null,
      transportMode: null as TransportModeSelectItem | null,
      geoReferenceMethod: '',
    });
  }
}
