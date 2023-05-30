import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {ContractNegotiationService} from '../../../core/services/api/contract-negotiation.service';
import {ContractOffer} from '../../../core/services/models/contract-offer';

@Component({
  selector: 'contract-offer-cards',
  templateUrl: './contract-offer-cards.component.html',
})
export class ContractOfferCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  contractOffers: ContractOffer[] = [];

  @Output()
  contractOfferClick = new EventEmitter<ContractOffer>();

  constructor(public contractNegotiationService: ContractNegotiationService) {}
}
