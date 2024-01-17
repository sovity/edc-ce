import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {DataOffer} from 'src/app/core/services/models/data-offer';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {ContractOffer} from '../../../core/services/models/contract-offer';

@Component({
  selector: 'contract-offer-mini-list',
  templateUrl: 'contract-offer-mini-list.component.html',
})
export class ContractOfferMiniListComponent {
  @Input()
  data!: DataOffer;

  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  cls = true;

  @Output()
  negotiateClick = new EventEmitter<ContractOffer>();

  constructor(public contractNegotiationService: ContractNegotiationService) {}
}
