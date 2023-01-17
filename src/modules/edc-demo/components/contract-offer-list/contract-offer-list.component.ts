import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {ContractOffer} from '../../models/contract-offer';
import {ContractNegotiationService} from '../../services/contract-negotiation.service';

@Component({
  selector: 'edc-demo-contract-offer-list',
  templateUrl: './contract-offer-list.component.html',
})
export class ContractOfferListComponent {
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
