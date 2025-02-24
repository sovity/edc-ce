import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {DataOffer} from '../../../core/services/models/data-offer';

@Component({
  selector: 'data-offer-cards',
  templateUrl: './data-offer-cards.component.html',
})
export class DataOfferCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  dataOffers: DataOffer[] = [];

  @Output()
  dataOfferClick = new EventEmitter<DataOffer>();

  constructor(public contractNegotiationService: ContractNegotiationService) {}

  isBusy(dataOffer: DataOffer): boolean {
    return dataOffer.contractOffers.some((it) =>
      this.contractNegotiationService.isBusy(it),
    );
  }
}
