import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {CatalogDataOfferMapped} from '../catalog-page/mapping/catalog-page-result-mapped';

@Component({
  selector: 'broker-data-offer-cards',
  templateUrl: './broker-data-offer-cards.component.html',
})
export class BrokerDataOfferCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  dataOffers: CatalogDataOfferMapped[] = [];

  @Output()
  dataOfferClick = new EventEmitter<CatalogDataOfferMapped>();
}
