/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  Output,
} from '@angular/core';
import {ContractAgreementCardMapped} from './contract-agreement-card-mapped';

@Component({
  selector: 'contract-agreement-cards',
  templateUrl: './contract-agreement-cards.component.html',
})
export class ContractAgreementCardsComponent {
  @HostBinding('class.flex')
  @HostBinding('class.flex-wrap')
  @HostBinding('class.gap-[10px]')
  cls = true;

  @Input()
  contractAgreements: ContractAgreementCardMapped[] = [];

  @Output()
  contractAgreementClick = new EventEmitter<ContractAgreementCardMapped>();

  onContractAgreementClick(contractAgreement: ContractAgreementCardMapped) {
    this.contractAgreementClick.emit(contractAgreement);
  }
}
