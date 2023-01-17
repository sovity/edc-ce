import {Component, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {first, map, switchMap} from 'rxjs/operators';
import {
  ContractDefinitionDto,
  ContractDefinitionService,
} from '../../../edc-dmgmt-client';
import {NotificationService} from '../../services/notification.service';
import {
  ConfirmDialogModel,
  ConfirmationDialogComponent,
} from '../confirmation-dialog/confirmation-dialog.component';
import {ContractDefinitionEditorDialogResult} from '../contract-definition-editor-dialog/contract-definition-editor-dialog-result';
import {ContractDefinitionEditorDialog} from '../contract-definition-editor-dialog/contract-definition-editor-dialog.component';

@Component({
  selector: 'edc-demo-contract-definition-viewer',
  templateUrl: './contract-definition-viewer.component.html',
  styleUrls: ['./contract-definition-viewer.component.scss'],
})
export class ContractDefinitionViewerComponent implements OnInit {
  filteredContractDefinitions$: Observable<ContractDefinitionDto[]> = of([]);
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(
    private contractDefinitionService: ContractDefinitionService,
    private notificationService: NotificationService,
    private readonly dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.filteredContractDefinitions$ = this.fetch$.pipe(
      switchMap(() =>
        this.contractDefinitionService.getAllContractDefinitions(),
      ),
      map((contractDefinitions) =>
        this.filterContractDefinitions(contractDefinitions, this.searchText),
      ),
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
      .pipe(first())
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
