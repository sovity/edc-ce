import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {from} from 'rxjs';
import {filter, switchMap, tap} from 'rxjs/operators';
import {
  AssetService,
  ContractAgreementDto,
  ContractAgreementService,
  TransferId,
} from '../../../edc-dmgmt-client';
import {Fetched} from '../../models/fetched';
import {TransferProcessStates} from '../../models/transfer-process-states';
import {ContractNegotiationService} from '../../services/contract-negotiation.service';
import {ContractOfferService} from '../../services/contract-offer.service';
import {NotificationService} from '../../services/notification.service';
import {ContractAgreementTransferDialogData} from '../contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-data';
import {ContractAgreementTransferDialogResult} from '../contract-agreement-transfer-dialog/contract-agreement-transfer-dialog-result';
import {ContractAgreementTransferDialog} from '../contract-agreement-transfer-dialog/contract-agreement-transfer-dialog.component';

interface RunningTransferProcess {
  processId: string;
  contractId: string;
  state: TransferProcessStates;
}

@Component({
  selector: 'app-contract-viewer',
  templateUrl: './contract-viewer.component.html',
  styleUrls: ['./contract-viewer.component.scss'],
})
export class ContractViewerComponent implements OnInit {
  contractList: Fetched<ContractAgreementDto[]> = Fetched.empty();
  private runningTransfers: RunningTransferProcess[] = [];
  private pollingHandleTransfer?: any;

  constructor(
    private contractNegotiationService: ContractNegotiationService,
    private contractAgreementService: ContractAgreementService,
    private assetService: AssetService,
    public dialog: MatDialog,
    private catalogService: ContractOfferService,
    private router: Router,
    private notificationService: NotificationService,
  ) {}

  private static isFinishedState(state: string): boolean {
    return ['COMPLETED', 'ERROR', 'ENDED'].includes(state);
  }

  ngOnInit(): void {
    this.contractAgreementService
      .getAllAgreements()
      .pipe(
        Fetched.wrap({
          failureMessage: 'Failed fetching contracts.',
        }),
      )
      .subscribe((contractList) => (this.contractList = contractList));
  }

  asDate(epochSeconds?: number): string {
    if (epochSeconds) {
      const d = new Date(0);
      d.setUTCSeconds(epochSeconds);
      return d.toLocaleDateString();
    }
    return '';
  }

  onTransferClicked(contract: ContractAgreementDto) {
    const data: ContractAgreementTransferDialogData = {
      contractId: contract.id,
    };
    const dialogRef = this.dialog.open(ContractAgreementTransferDialog, {data});

    dialogRef
      .afterClosed()
      .pipe(filter((it) => !!it))
      .subscribe((result: ContractAgreementTransferDialogResult) => {
        this.startPolling(result.transferProcessId, result.contractId);
      });
  }

  isTransferInProgress(contractId: string): boolean {
    return !!this.runningTransfers.find((rt) => rt.contractId === contractId);
  }

  private startPolling(transferProcessId: TransferId, contractId: string) {
    // track this transfer process
    this.runningTransfers.push({
      processId: transferProcessId.id!,
      state: TransferProcessStates.REQUESTED,
      contractId: contractId,
    });

    if (!this.pollingHandleTransfer) {
      this.pollingHandleTransfer = setInterval(
        this.pollRunningTransfers(),
        1000,
      );
    }
  }

  private pollRunningTransfers() {
    return () => {
      from(this.runningTransfers) //create from array
        .pipe(
          // fetch from API
          switchMap((t) =>
            this.contractNegotiationService.getTransferProcessesById(
              t.processId,
            ),
          ),
          // only use finished ones
          filter((tpDto) =>
            ContractViewerComponent.isFinishedState(tpDto.state),
          ),
          // remove from in-progress
          tap((tpDto) => {
            this.runningTransfers = this.runningTransfers.filter(
              (rtp) => rtp.processId !== tpDto.id,
            );
            this.notificationService.showInfo(
              `Transfer [${tpDto.id}] complete!`,
              'Show me!',
              () => {
                this.router.navigate(['/transfer-history']);
              },
            );
          }),
        )
        .subscribe({
          next: () => {
            // clear interval if necessary
            if (this.runningTransfers.length === 0) {
              clearInterval(this.pollingHandleTransfer);
              this.pollingHandleTransfer = undefined;
            }
          },
          error: (error) => this.notificationService.showError(error),
        });
    };
  }
}
