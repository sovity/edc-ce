import {Injectable} from '@angular/core';
import {TransferProcessDto} from './api/legacy-managent-api-client';

@Injectable({providedIn: 'root'})
export class TransferProcessUtils {
  isIncoming(transferProcess: TransferProcessDto): boolean {
    return transferProcess.dataRequest.connectorId === 'consumer';
  }

  isOutgoing(transferProcess: TransferProcessDto): boolean {
    return !this.isIncoming(transferProcess);
  }
}
