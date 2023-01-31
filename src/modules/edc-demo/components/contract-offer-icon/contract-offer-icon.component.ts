import {Component, Input} from '@angular/core';
import {ContractOffer} from '../../models/contract-offer';
import {ContractNegotiationService} from '../../services/contract-negotiation.service';

@Component({
  selector: 'edc-demo-contract-offer-icon',
  template: `
    <!-- Negotiation Success Indicator -->
    <div
      *ngIf="contractNegotiationService.isNegotiated(contractOffer)"
      style="position: absolute;">
      <mat-icon
        style="
            margin-top: 3px;
            margin-left: 9px;
            font-weight: bold;
            color: limegreen;
            font-size: 40px;
            width: 40px;
            height: 40px;
          ">
        done
      </mat-icon>
    </div>

    <!-- Icon -->
    <mat-icon
      style="font-size: 40px; width: 40px; height: 40px; min-width: 40px"
      >sim_card</mat-icon
    >
  `,
})
export class ContractOfferIconComponent {
  @Input()
  contractOffer!: ContractOffer;

  constructor(public contractNegotiationService: ContractNegotiationService) {}
}
