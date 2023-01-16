import {Pipe, PipeTransform} from '@angular/core';
import {DataSubcategorySelectItem} from "./data-subcategory-select-item";
import {DataCategorySelectItem} from "../data-category-select/data-category-select-item";
import {DataSubcategorySelectItemService} from "./data-subcategory-select-item.service";

@Pipe({
  name: "dataSubcategoryItems"
})
export class DataSubcategoryItemsPipe implements PipeTransform {
  constructor(private items: DataSubcategorySelectItemService) {
  }

  transform(dataCategory: DataCategorySelectItem | null): DataSubcategorySelectItem[] {
    return this.items.findByDataCategory(dataCategory);
  }


}
