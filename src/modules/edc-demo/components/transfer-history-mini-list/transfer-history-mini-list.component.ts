import {Component, HostBinding, Input, TrackByFunction} from '@angular/core';
import {ContractAgreementTransferProcess} from '@sovity.de/edc-client';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';

@Component({
  selector: 'transfer-history-mini-list',
  templateUrl: 'transfer-history-mini-list.component.html',
})
export class TransferHistoryMiniListComponent {
  @Input()
  contractAgreement!: ContractAgreementCardMapped;

  @HostBinding('class.flex')
  @HostBinding('class.flex-col')
  @HostBinding('class.space-y-[10px]')
  cls = true;
  trackBy: TrackByFunction<ContractAgreementTransferProcess> = (_, it) =>
    it.transferProcessId;
}
