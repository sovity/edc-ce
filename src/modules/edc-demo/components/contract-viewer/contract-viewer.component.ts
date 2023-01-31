import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {Observable, from, of} from 'rxjs';
import {filter, first, map, switchMap, tap} from 'rxjs/operators';
import {AppConfigService} from '../../../app/config/app-config.service';
import {
  AssetService,
  ContractAgreementDto,
  ContractAgreementService,
  DataAddressDto,
  TransferId,
  TransferProcessService,
} from '../../../edc-dmgmt-client';
import {TransferProcessStates} from '../../models/transfer-process-states';
import {AssetPropertyMapper} from '../../services/asset-property-mapper';
import {CatalogBrowserService} from '../../services/catalog-browser.service';
import {NotificationService} from '../../services/notification.service';
import {CatalogBrowserTransferDialog} from '../catalog-browser-transfer-dialog/catalog-browser-transfer-dialog.component';

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
  contracts$: Observable<ContractAgreementDto[]> = of([]);
  private runningTransfers: RunningTransferProcess[] = [];
  private pollingHandleTransfer?: any;

  constructor(
    private contractAgreementService: ContractAgreementService,
    private assetService: AssetService,
    public dialog: MatDialog,
    private transferService: TransferProcessService,
    private catalogService: CatalogBrowserService,
    private router: Router,
    private notificationService: NotificationService,
    private appConfigService: AppConfigService,
    private assetPropertyMapper: AssetPropertyMapper,
  ) {}

  private static isFinishedState(state: string): boolean {
    return ['COMPLETED', 'ERROR', 'ENDED'].includes(state);
  }

  ngOnInit(): void {
    this.contracts$ = this.contractAgreementService.getAllAgreements();
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
    const dialogRef = this.dialog.open(CatalogBrowserTransferDialog);

    dialogRef
      .afterClosed()
      .pipe(
        map((result) => result.dataDestination as DataAddressDto),
        first(),
        switchMap((dataAddressDto) =>
          this.contractAgreementService.initiateTransfer(
            contract.id,
            dataAddressDto,
          ),
        ),
      )
      .subscribe({
        next: (transferId) => {
          this.startPolling(transferId, contract.id!);
        },
        error: (error) => {
          console.error(error);
          this.notificationService.showError('Error initiating transfer');
        },
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
            this.catalogService.getTransferProcessesById(t.processId),
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
        .subscribe(
          () => {
            // clear interval if necessary
            if (this.runningTransfers.length === 0) {
              clearInterval(this.pollingHandleTransfer);
              this.pollingHandleTransfer = undefined;
            }
          },
          (error) => this.notificationService.showError(error),
        );
    };
  }
}
