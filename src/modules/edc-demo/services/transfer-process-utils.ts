import {Injectable} from '@angular/core';
import {TransferProcessDto} from '../../edc-dmgmt-client';

@Injectable({providedIn: 'root'})
export class TransferProcessUtils {
  isIncoming(transferProcess: TransferProcessDto): boolean {
    return transferProcess.dataRequest.connectorId === 'consumer';
  }

  isOutgoing(transferProcess: TransferProcessDto): boolean {
    return !this.isIncoming(transferProcess);
  }
}
