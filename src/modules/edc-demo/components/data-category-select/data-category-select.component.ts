import { Component, HostBinding, Input } from '@angular/core';
import { FormControl } from "@angular/forms";
import { DATA_CATEGORY_SELECT_DATA } from './data-category-select-data';
import { DataCategorySelectItem } from "./data-category-select-item";

@Component({
  selector: 'data-category-select',
  templateUrl: 'data-category-select.component.html',
})
export class DataCategorySelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<DataCategorySelectItem | null>

  @HostBinding("class.flex")
  @HostBinding("class.flex-row")
  cls = true

  items = DATA_CATEGORY_SELECT_DATA

  compareWith(a: DataCategorySelectItem | null, b: DataCategorySelectItem | null): boolean {
    return a?.id === b?.id;
  }
}
