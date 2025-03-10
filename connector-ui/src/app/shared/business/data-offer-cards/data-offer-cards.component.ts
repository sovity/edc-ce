/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
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
