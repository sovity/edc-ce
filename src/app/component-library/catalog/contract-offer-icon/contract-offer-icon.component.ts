import {Component, Input} from '@angular/core';
import {ContractNegotiationService} from '../../../core/services/api/contract-negotiation.service';
import {ContractOffer} from '../../../core/services/models/contract-offer';

@Component({
  selector: 'contract-offer-icon',
  template: `
    <!-- Negotiation Success Indicator -->
    <div
      *ngIf="contractNegotiationService.isNegotiated(contractOffer)"
      style="position: absolute;">
      <mat-icon
        class="mat-icon-[40px]"
        style="
            margin-top: 3px;
            margin-left: 9px;
            font-weight: bold;
            color: limegreen;
          ">
        done
      </mat-icon>
    </div>

    <!-- Icon -->
    <mat-icon class="mat-icon-[40px]">sim_card</mat-icon>
  `,
})
export class ContractOfferIconComponent {
  @Input()
  contractOffer!: ContractOffer;

  constructor(public contractNegotiationService: ContractNegotiationService) {}
}
