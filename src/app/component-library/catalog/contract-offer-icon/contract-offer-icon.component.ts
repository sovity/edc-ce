import {Component, Input} from '@angular/core';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {DataOffer} from '../../../core/services/models/data-offer';

@Component({
  selector: 'contract-offer-icon',
  template: `
    <!-- Negotiation Success Indicator -->
    <div *ngIf="isNegotiated()" style="position: absolute;">
      <mat-icon
        class="mat-card-avatar-icon"
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
    <mat-icon class="mat-card-avatar-icon">sim_card</mat-icon>
  `,
})
export class ContractOfferIconComponent {
  @Input()
  dataOffer!: DataOffer;

  constructor(public contractNegotiationService: ContractNegotiationService) {}

  isNegotiated(): boolean {
    return this.dataOffer?.contractOffers?.some((it) =>
      this.contractNegotiationService.isNegotiated(it),
    );
  }
}
