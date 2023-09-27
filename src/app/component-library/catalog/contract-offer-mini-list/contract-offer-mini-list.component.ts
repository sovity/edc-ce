import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {ContractOffer} from '../../../core/services/models/contract-offer';

@Component({
  selector: 'contract-offer-mini-list',
  templateUrl: 'contract-offer-mini-list.component.html',
})
export class ContractOfferMiniListComponent {
  @Input()
  contractOffers!: ContractOffer[];

  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  cls = true;

  @Output()
  negotiateClick = new EventEmitter<ContractOffer>();

  constructor(public contractNegotiationService: ContractNegotiationService) {}
}
