import {PaymentModalitySelectItem} from './payment-modality-select-item';
import {Injectable} from "@angular/core";
import {PAYMENT_MODALITY_SELECT_DATA} from "./payment-modality-select-data";

/**
 * Access list of available PaymentModalitySelectItems
 */
@Injectable({providedIn: 'root'})
export class PaymentModalitySelectItemService {
  items: PaymentModalitySelectItem[];
  itemsById: Map<string, PaymentModalitySelectItem>;

  constructor() {
    this.items = PAYMENT_MODALITY_SELECT_DATA;
    this.itemsById = this.buildItemsMap();
  }

  /**
   * Find PaymentModalitySelectItem by id
   * @param id language select item id
   */
  findById(id: string): PaymentModalitySelectItem {
    const item = this.itemsById.get(id);
    if (item != null) {
      return item;
    }
    return {
      id,
      label: `Unknown (${id})`,
      comment: ''
    };
  }

  free(): PaymentModalitySelectItem {
    return this.findById("FREE")
  }

  private buildItemsMap(): Map<string, PaymentModalitySelectItem> {
    return new Map(PAYMENT_MODALITY_SELECT_DATA.map(it => [it.id, it]))
  }
}
