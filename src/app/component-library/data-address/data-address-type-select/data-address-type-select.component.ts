import {Component, HostBinding, Input, OnChanges} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ActiveFeatureSet} from 'src/app/core/config/active-feature-set';
import {SimpleChangesTyped} from '../../../core/utils/angular-utils';
import {DataAddressType} from './data-address-type';
import {dataAddressTypeSelectItems} from './data-address-type-select-items';
import {DataAddressTypeSelectMode} from './data-address-type-select-mode';

@Component({
  selector: 'data-address-type-select',
  templateUrl: 'data-address-type-select.component.html',
})
export class DataAddressTypeSelectComponent implements OnChanges {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<DataAddressType | null>;

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  @Input()
  mode: DataAddressTypeSelectMode = 'Datasource-Create';

  constructor(private activeFeatureSet: ActiveFeatureSet) {}

  items = dataAddressTypeSelectItems(this.mode, this.activeFeatureSet);
  ngOnChanges(changes: SimpleChangesTyped<DataAddressTypeSelectComponent>) {
    if (changes.mode) {
      this.items = dataAddressTypeSelectItems(this.mode, this.activeFeatureSet);
    }
  }
}
