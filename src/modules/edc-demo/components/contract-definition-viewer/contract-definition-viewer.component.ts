import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject} from 'rxjs';
import {filter, map, switchMap} from 'rxjs/operators';
import {
  ContractDefinitionDto,
  ContractDefinitionService,
} from '../../../edc-dmgmt-client';
import {Fetched} from '../../models/fetched';
import {NotificationService} from '../../services/notification.service';
import {
  ConfirmDialogModel,
  ConfirmationDialogComponent,
} from '../confirmation-dialog/confirmation-dialog.component';
import {ContractDefinitionEditorDialogResult} from '../contract-definition-editor-dialog/contract-definition-editor-dialog-result';
import {ContractDefinitionEditorDialog} from '../contract-definition-editor-dialog/contract-definition-editor-dialog.component';

export interface ContractDefinitionList {
  filteredContractDefinitions: ContractDefinitionDto[];
  numTotalContractDefinitions: number;
}

@Component({
  selector: 'edc-demo-contract-definition-viewer',
  templateUrl: './contract-definition-viewer.component.html',
  styleUrls: ['./contract-definition-viewer.component.scss'],
})
export class ContractDefinitionViewerComponent implements OnInit {
  contractDefinitionList: Fetched<ContractDefinitionList> = Fetched.empty();
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private contractDefinitionService: ContractDefinitionService,
    private notificationService: NotificationService,
    private readonly dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.fetch$
      .pipe(
        switchMap(() => {
          return this.contractDefinitionService
            .getAllContractDefinitions()
            .pipe(
              map(
                (contractDefinitions): ContractDefinitionList => ({
                  filteredContractDefinitions: this.filterContractDefinitions(
                    contractDefinitions,
                    this.searchText,
                  ),
                  numTotalContractDefinitions: contractDefinitions.length,
                }),
              ),
              Fetched.wrap({
                failureMessage: 'Failed fetching contract definitions.',
              }),
            );
        }),
      )
      .subscribe(
        (contractDefinitionList) =>
          (this.contractDefinitionList = contractDefinitionList),
      );
  }

  onSearch() {
    this.refresh();
  }

  onDelete(contractDefinition: ContractDefinitionDto) {
    const dialogData = ConfirmDialogModel.forDelete(
      'contract definition',
      contractDefinition.id,
    );

    const ref = this.dialog.open(ConfirmationDialogComponent, {
      maxWidth: '20%',
      data: dialogData,
    });

    ref.afterClosed().subscribe((res) => {
      if (res) {
        this.contractDefinitionService
          .deleteContractDefinition(contractDefinition.id)
          .subscribe(() => this.fetch$.next(null));
      }
    });
  }

  onCreate() {
    const dialogRef = this.dialog.open(ContractDefinitionEditorDialog);
    dialogRef
      .afterClosed()
      .pipe(filter((it) => it))
      .subscribe((result: ContractDefinitionEditorDialogResult) => {
        if (result.refreshList) {
          this.refresh();
        }
      });
  }

  private filterContractDefinitions(
    contractDefinitions: ContractDefinitionDto[],
    searchText: string,
  ): ContractDefinitionDto[] {
    if (!searchText) {
      return contractDefinitions;
    }
    return contractDefinitions.filter((contractDefinition) =>
      contractDefinition.id.toLowerCase().includes(this.searchText),
    );
  }

  private refresh() {
    this.fetch$.next(null);
  }
}
