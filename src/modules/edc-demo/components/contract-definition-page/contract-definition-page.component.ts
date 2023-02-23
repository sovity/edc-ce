import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {BehaviorSubject, Observable, distinctUntilChanged} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {Fetched} from '../../models/fetched';
import {NotificationService} from '../../services/notification.service';
import {value$} from '../../utils/form-group-utils';
import {ContractDefinitionCardBuilder} from '../contract-definition-cards/contract-definition-card-builder';
import {ContractDefinitionEditorDialogResult} from '../contract-definition-editor-dialog/contract-definition-editor-dialog-result';
import {ContractDefinitionEditorDialog} from '../contract-definition-editor-dialog/contract-definition-editor-dialog.component';
import {ContractDefinitionPageData} from './contract-definition-page.data';
import {ContractDefinitionPageService} from './contract-definition-page.service';

@Component({
  selector: 'app-contract-definition-page',
  templateUrl: './contract-definition-page.component.html',
  styleUrls: ['./contract-definition-page.component.scss'],
})
export class ContractDefinitionPageComponent implements OnInit {
  contractDefinitionList: Fetched<ContractDefinitionPageData> = Fetched.empty();
  searchText = new FormControl<string>('');
  deleteBusy = false;

  private fetch$ = new BehaviorSubject(null);

  constructor(
    private contractDefinitionPageService: ContractDefinitionPageService,
    private notificationService: NotificationService,
    private contractDefinitionCardBuilder: ContractDefinitionCardBuilder,
    private readonly dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.contractDefinitionPageService
      .contractDefinitionPageData$(this.fetch$, this.searchText$())
      .subscribe((contractDefinitionList) => {
        this.contractDefinitionList = contractDefinitionList;
      });
  }

  onCreate() {
    const dialogRef = this.dialog.open(ContractDefinitionEditorDialog);
    dialogRef
      .afterClosed()
      .pipe(
        map((it) => it as ContractDefinitionEditorDialogResult | null),
        filter((it) => !!it?.refreshList),
      )
      .subscribe(() => this.refresh());
  }

  refresh() {
    this.fetch$.next(null);
  }

  private searchText$(): Observable<string> {
    return (value$(this.searchText) as Observable<string>).pipe(
      map((it) => (it ?? '').trim()),
      distinctUntilChanged(),
    );
  }
}
