import {Component, Input, OnChanges} from '@angular/core';
import {FormControl} from '@angular/forms';
import {SimpleChangesTyped} from '../../../core/utils/angular-utils';
import {DataAddressType} from '../../data-address/data-address-type-select/data-address-type';
import {dataAddressTypeSelectItems} from '../../data-address/data-address-type-select/data-address-type-select-items';
import {DataAddressTypeSelectMode} from '../../data-address/data-address-type-select/data-address-type-select-mode';

@Component({
  selector: 'edit-asset-form-data-address-type-select',
  templateUrl: 'edit-asset-form-data-address-type-select.component.html',
})
export class EditAssetFormDataAddressTypeSelectComponent implements OnChanges {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<DataAddressType | null>;

  @Input()
  mode: DataAddressTypeSelectMode = 'Datasource-Create';

  items = dataAddressTypeSelectItems(this.mode);
  ngOnChanges(
    changes: SimpleChangesTyped<EditAssetFormDataAddressTypeSelectComponent>,
  ) {
    if (changes.mode) {
      this.items = dataAddressTypeSelectItems(this.mode);
    }
  }
}
