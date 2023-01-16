import {TransportModeSelectItem} from './transport-mode-select-item';
import {Injectable} from "@angular/core";
import {TRANSPORT_MODE_SELECT_DATA} from "./transport-mode-select-data";
import {associateBy} from "../../utils/map-utils";

/**
 * Access list of available TransportModeSelectItems
 */
@Injectable({providedIn: 'root'})
export class TransportModeSelectItemService {
  itemsById = associateBy(TRANSPORT_MODE_SELECT_DATA, it => it.id);

  /**
   * Find TransportModeSelectItem by id
   * @param id language select item id
   */
  findById(id: string): TransportModeSelectItem {
    const item = this.itemsById.get(id);
    if (item != null) {
      return item;
    }
    return {
      id,
      label: `Unknown (${id})`
    };
  }
}
