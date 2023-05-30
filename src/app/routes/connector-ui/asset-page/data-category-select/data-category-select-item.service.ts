import {Injectable} from '@angular/core';
import {DATA_CATEGORY_SELECT_DATA} from './data-category-select-data';
import {DataCategorySelectItem} from './data-category-select-item';

/**
 * Access list of available DataCategorySelectItems
 */
@Injectable({providedIn: 'root'})
export class DataCategorySelectItemService {
  itemsById: Map<string, DataCategorySelectItem>;

  constructor() {
    this.itemsById = this.buildItemsMap();
  }

  /**
   * Find DataCategorySelectItem by id
   * @param id language select item id
   */
  findById(id: string): DataCategorySelectItem {
    const item = this.itemsById.get(id);
    if (item != null) {
      return item;
    }
    return {
      id,
      label: `Unknown (${id})`,
    };
  }

  private buildItemsMap(): Map<string, DataCategorySelectItem> {
    return new Map(DATA_CATEGORY_SELECT_DATA.map((it) => [it.id, it]));
  }
}
