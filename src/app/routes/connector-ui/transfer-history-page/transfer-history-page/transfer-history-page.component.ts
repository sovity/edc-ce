import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {map} from 'rxjs/operators';
import {
  ConfirmDialogModel,
  ConfirmationDialogComponent,
} from '../../../../component-library/confirmation-dialog/confirmation-dialog/confirmation-dialog.component';
import {JsonDialogComponent} from '../../../../component-library/json-dialog/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../../../component-library/json-dialog/json-dialog/json-dialog.data';
import {
  TransferProcessDto,
  TransferProcessService,
} from '../../../../core/services/api/legacy-managent-api-client';
import {Fetched} from '../../../../core/services/models/fetched';

@Component({
  selector: 'transfer-history-page',
  templateUrl: './transfer-history-page.component.html',
  styleUrls: ['./transfer-history-page.component.scss'],
})
export class TransferHistoryPageComponent implements OnInit {
  columns: string[] = [
    'id',
    'creationDate',
    'state',
    'lastUpdated',
    'connectorId',
    'assetId',
    'contractId',
    'details',
  ];
  transferProcessesList: Fetched<{
    transferProcesses: Array<TransferProcessDto>;
  }> = Fetched.empty();

  constructor(
    private transferProcessService: TransferProcessService,
    private dialog: MatDialog,
  ) {}

  onTransferHistoryDetailsClick(item: TransferProcessDto) {
    const data: JsonDialogData = {
      title: item.id,
      subtitle: 'Transfer History Details',
      icon: 'assignment',
      objectForJson: item,
    };
    this.dialog.open(JsonDialogComponent, {data});
  }

  ngOnInit(): void {
    this.loadTransferProcesses();
  }

  onDeprovision(transferProcess: TransferProcessDto): void {
    const dialogData = new ConfirmDialogModel(
      'Confirm deprovision',
      `Deprovisioning resources for transfer [${transferProcess.id}] will take some time and once started, it cannot be stopped.`,
    );
    dialogData.confirmColor = 'warn';
    dialogData.confirmText = 'Confirm';
    dialogData.cancelText = 'Abort';
    const ref = this.dialog.open(ConfirmationDialogComponent, {
      maxWidth: '20%',
      data: dialogData,
    });

    ref.afterClosed().subscribe((res) => {
      if (res) {
        this.transferProcessService
          .deprovisionTransferProcess(transferProcess.id)
          .subscribe(() => this.loadTransferProcesses());
      }
    });
  }

  loadTransferProcesses() {
    this.transferProcessService
      .getAllTransferProcesses(0, 10_000_000)
      .pipe(
        map((transferProcesses) => ({
          transferProcesses: transferProcesses.sort(function (a, b) {
            return (
              b.createdTimestamp?.valueOf()! - a.createdTimestamp?.valueOf()!
            );
          }),
        })),
        Fetched.wrap({
          failureMessage: 'Failed fetching transfer history.',
        }),
      )
      .subscribe(
        (transferProcessesList) =>
          (this.transferProcessesList = transferProcessesList),
      );
  }

  asDate(epochMillis?: number) {
    return epochMillis ? new Date(epochMillis).toLocaleDateString() : '';
  }
}
