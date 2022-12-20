import {Component, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {first, map, switchMap} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {
  ContractDefinitionEditorDialog
} from '../contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {
  ContractDefinitionRequestDto,
  ContractDefinitionResponseDto,
  ContractDefinitionService
} from "../../../mgmt-api-client";
import {ConfirmationDialogComponent, ConfirmDialogModel} from "../confirmation-dialog/confirmation-dialog.component";
import {NotificationService} from "../../services/notification.service";


@Component({
  selector: 'edc-demo-contract-definition-viewer',
  templateUrl: './contract-definition-viewer.component.html',
  styleUrls: ['./contract-definition-viewer.component.scss']
})
export class ContractDefinitionViewerComponent implements OnInit {

  filteredContractDefinitions$: Observable<ContractDefinitionResponseDto[]> = of([]);
  searchText = '';
  private fetch$ = new BehaviorSubject(null);

  constructor(private contractDefinitionService: ContractDefinitionService,
              private notificationService: NotificationService,
              private readonly dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.filteredContractDefinitions$ = this.fetch$
      .pipe(
        switchMap(() => {
          const contractDefinitions$ = this.contractDefinitionService.getAllContractDefinitions();
          return !!this.searchText ?
            contractDefinitions$.pipe(map(contractDefinitions => contractDefinitions.filter(contractDefinition => contractDefinition.id!.toLowerCase().includes(this.searchText))))
            :
            contractDefinitions$;
        }));
  }

  onSearch() {
    this.fetch$.next(null);
  }

  onDelete(contractDefinition: ContractDefinitionResponseDto) {
    const dialogData = ConfirmDialogModel.forDelete("contract definition", contractDefinition.id!);

    const ref = this.dialog.open(ConfirmationDialogComponent, {maxWidth: '20%', data: dialogData});

    ref.afterClosed().subscribe(res => {
      if (res) {
        this.contractDefinitionService.deleteContractDefinition(contractDefinition.id!).subscribe(() => this.fetch$.next(null));
      }
    });

  }

  onCreate() {
    const dialogRef = this.dialog.open(ContractDefinitionEditorDialog);
    dialogRef.afterClosed().pipe(first()).subscribe((result: { contractDefinition?: ContractDefinitionRequestDto }) => {
      const newContractDefinition = result?.contractDefinition;
      if (newContractDefinition) {
        this.contractDefinitionService.createContractDefinition(newContractDefinition).subscribe(() => this.fetch$.next(null),
          error => this.notificationService.showError("Contract definition cannot be created"),
          () => this.notificationService.showInfo("Contract definition created"));
      }
    });
  }

}
