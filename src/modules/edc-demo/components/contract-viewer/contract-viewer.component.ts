import {Component, Inject, OnInit} from '@angular/core';
import {
  AssetService,
  ContractAgreementDto,
  ContractAgreementService, IdResponseDto,
  TransferProcessService,
  TransferRequestDto
} from "../../../mgmt-api-client";
import {from, Observable, of} from "rxjs";
import {Asset} from "../../models/asset";
import {filter, first, map, switchMap, tap} from "rxjs/operators";
import {NotificationService} from "../../services/notification.service";
import {
  CatalogBrowserTransferDialog
} from "../catalog-browser-transfer-dialog/catalog-browser-transfer-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {CatalogBrowserService} from "../../services/catalog-browser.service";
import {Router} from "@angular/router";
import {TransferProcessStates} from "../../models/transfer-process-states";

interface RunningTransferProcess {
  processId: string;
  contractId: string;
  state: TransferProcessStates;
}

@Component({
  selector: 'app-contract-viewer',
  templateUrl: './contract-viewer.component.html',
  styleUrls: ['./contract-viewer.component.scss']
})
export class ContractViewerComponent implements OnInit {

  contracts$: Observable<ContractAgreementDto[]> = of([]);
  private runningTransfers: RunningTransferProcess[] = [];
  private pollingHandleTransfer?: any;

  constructor(private contractAgreementService: ContractAgreementService,
              private assetService: AssetService,
              public dialog: MatDialog,
              @Inject('HOME_CONNECTOR_STORAGE_ACCOUNT') private homeConnectorStorageAccount: string,
              private transferService: TransferProcessService,
              private catalogService: CatalogBrowserService,
              private router: Router,
              private notificationService: NotificationService) {
  }

  private static isFinishedState(state: string): boolean {
    return [
      "COMPLETED",
      "ERROR",
      "ENDED"].includes(state);
  }

  ngOnInit(): void {
    this.contracts$ = this.contractAgreementService.getAllAgreements();
  }

  asDate(epochSeconds?: number): string {
    if(epochSeconds){
      const d = new Date(0);
      d.setUTCSeconds(epochSeconds);
      return d.toLocaleDateString();
    }
    return '';
  }

  getAsset(assetId?: string): Observable<Asset> {
    return assetId ? this.assetService.getAsset(assetId).pipe(map(a => new Asset(a.properties!))) : of();
  }

  onTransferClicked(contract: ContractAgreementDto) {
    const dialogRef = this.dialog.open(CatalogBrowserTransferDialog);

    dialogRef.afterClosed().pipe(first()).subscribe(result => {
      const storageTypeId: string = result.storageTypeId;
      if (storageTypeId !== 'AzureStorage') {
        this.notificationService.showError("Only storage type \"AzureStorage\" is implemented currently!")
        return;
      }
      this.createTransferRequest(contract, storageTypeId)
        .pipe(switchMap(trq => this.transferService.initiateTransfer(trq)))
        .subscribe(transferId => {
          this.startPolling(transferId, contract.id!);
        }, error => {
          console.error(error);
          this.notificationService.showError("Error initiating transfer");
        });
    });
  }

  isTransferInProgress(contractId: string): boolean {
    return !!this.runningTransfers.find(rt => rt.contractId === contractId);
  }

  private createTransferRequest(contract: ContractAgreementDto, storageTypeId: string): Observable<TransferRequestDto> {
    return this.getOfferedAssetForId(contract.assetId!).pipe(map(offeredAsset => {
      return {
        assetId: offeredAsset.id,
        contractId: contract.id,
        connectorId: "consumer", //doesn't matter, but cannot be null
        dataDestination: {
          properties: {
            "type": storageTypeId,
            account: this.homeConnectorStorageAccount, // CAUTION: hardcoded value for AzureBlob
            // container: omitted, so it will be auto-assigned by the EDC runtime
          }
        },
        managedResources: true,
        transferType: {isFinite: true}, //must be there, otherwise NPE on backend
        connectorAddress: offeredAsset.originator,
        protocol: 'ids-multipart'
      };
    }));

  }

  /**
   * This method is used to obtain that URL of the connector that is offering a particular asset from the catalog.
   * This is a bit of a hack, because currently there is no "clean" way to get the counter-party's URL for a ContractAgreement.
   *
   * @param assetId Asset ID of the asset that is associated with the contract.
   */
  private getOfferedAssetForId(assetId: string): Observable<Asset> {
    return this.catalogService.getContractOffers()
      .pipe(
        map(offers => offers.find(o => `${o.asset.id}` === assetId)),
        map(o => {
          if (o) return o.asset;
          else throw new Error(`No offer found for asset ID ${assetId}`);
        }))
  }

  private startPolling(transferProcessId: IdResponseDto, contractId: string) {
    // track this transfer process
    this.runningTransfers.push({
      processId: transferProcessId.id!,
      state: TransferProcessStates.REQUESTED,
      contractId: contractId
    });

    if (!this.pollingHandleTransfer) {
      this.pollingHandleTransfer = setInterval(this.pollRunningTransfers(), 1000);
    }

  }

  private pollRunningTransfers() {
    return () => {
      from(this.runningTransfers) //create from array
        .pipe(switchMap(t => this.catalogService.getTransferProcessesById(t.processId)), // fetch from API
          filter(tpDto => ContractViewerComponent.isFinishedState(tpDto.state!)), // only use finished ones
          tap(tpDto => {
            // remove from in-progress
            this.runningTransfers = this.runningTransfers.filter(rtp => rtp.processId !== tpDto.id)
            this.notificationService.showInfo(`Transfer [${tpDto.id}] complete!`, "Show me!", () => {
              this.router.navigate(['/transfer-history'])
            })
          }),
        ).subscribe(() => {
        // clear interval if necessary
        if (this.runningTransfers.length === 0) {
          clearInterval(this.pollingHandleTransfer);
          this.pollingHandleTransfer = undefined;
        }
      }, error => this.notificationService.showError(error))
    }

  }
}
