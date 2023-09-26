import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {BrokerDataOffer} from '../catalog-page/mapping/broker-data-offer';

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
  dataOffers: BrokerDataOffer[] = [];

  @Output()
  dataOfferClick = new EventEmitter<BrokerDataOffer>();
}
