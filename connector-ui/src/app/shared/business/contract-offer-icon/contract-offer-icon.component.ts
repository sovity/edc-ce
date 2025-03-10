/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Component, Input} from '@angular/core';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {DataOffer} from '../../../core/services/models/data-offer';

@Component({
  selector: 'contract-offer-icon',
  template: `
    <!-- Negotiation Success Overlay Icon -->
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

    <!-- Live Icon -->
    <mat-icon *ngIf="!isOnRequestAsset()" class="mat-card-avatar-icon"
      >sim_card</mat-icon
    >

    <!-- On Request Icon -->
    <mat-icon
      *ngIf="this.isOnRequestAsset()"
      class="mat-card-avatar-icon"
      style="transform: scaleX(-1);"
      >contact_page</mat-icon
    >
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

  isOnRequestAsset(): boolean {
    return this.dataOffer.asset.dataSourceAvailability === 'ON_REQUEST';
  }
}
