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
import {DataOffer} from 'src/app/core/services/models/data-offer';
import {ContractNegotiationService} from '../../../core/services/contract-negotiation.service';
import {ContractOffer} from '../../../core/services/models/contract-offer';
import {PropertyGridField} from '../../common/property-grid/property-grid-field';

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

  contractOfferIdGroup(id: string): PropertyGridField[] {
    return [
      {
        icon: 'category',
        label: 'Contract Offer Id',
        text: this.data.contractOffers.find((it) => it.contractOfferId == id)
          ?.contractOfferId,
        additionalClasses: 'min-h-fit h-fit break-all',
      },
    ];
  }
}
