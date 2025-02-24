import {Component, HostBinding, Input} from '@angular/core';
import {ContractAgreementCardMapped} from '../../../routes/connector-ui/contract-agreement-page/contract-agreement-cards/contract-agreement-card-mapped';

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
}
