import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {
  BehaviorSubject,
  Observable,
  Subject,
  concat,
  distinctUntilChanged,
  of,
  share,
} from 'rxjs';
import {filter, map, takeUntil} from 'rxjs/operators';
import {Fetched} from '../../models/fetched';
import {value$} from '../../utils/form-group-utils';
import {filterNotNull} from '../../utils/rxjs-utils';
import {AssetDetailDialogData} from '../asset-detail-dialog/asset-detail-dialog-data';
import {AssetDetailDialog} from '../asset-detail-dialog/asset-detail-dialog.component';
import {ContractAgreementCardMapped} from '../contract-agreement-cards/contract-agreement-card-mapped';
import {ContractAgreementPageData} from './contract-agreement-page.data';
import {ContractAgreementPageService} from './contract-agreement-page.service';

@Component({
  selector: 'app-contract-agreement-page',
  templateUrl: './contract-agreement-page.component.html',
  styleUrls: ['./contract-agreement-page.component.scss'],
})
export class ContractAgreementPageComponent implements OnInit, OnDestroy {
  page: Fetched<ContractAgreementPageData> = Fetched.empty();
  page$: Observable<Fetched<ContractAgreementPageData>> = new Subject();
  searchText = new FormControl<string>('');
  deleteBusy = false;

  private fetch$ = new BehaviorSubject(null);

  constructor(
    private matDialog: MatDialog,
    private contractAgreementPageService: ContractAgreementPageService,
  ) {}

  ngOnInit(): void {
    this.page$ = this.contractAgreementPageService
      .contractAgreementPageData$(this.fetch$, 5000, this.searchText$())
      .pipe(takeUntil(this.ngOnDestroy$), share());
    this.page$.subscribe((contractAgreementList) => {
      this.page = contractAgreementList;
    });
  }

  onContractAgreementClick(card: ContractAgreementCardMapped) {
    const data$: Observable<AssetDetailDialogData> = this.card$(
      card.contractAgreementId,
    ).pipe(map((it) => AssetDetailDialogData.forContractAgreement(it)));
    this.matDialog.open(AssetDetailDialog, {
      data: data$,
      maxHeight: '90vh',
    });
  }

  refresh() {
    this.fetch$.next(null);
  }

  private card$(
    contractAgreementId: string,
  ): Observable<ContractAgreementCardMapped> {
    return concat(of(this.page), this.page$).pipe(
      filter((fetched) => fetched.isReady),
      map((fetched) => fetched.data),
      map((page) =>
        page.contractAgreements.find(
          (it) => it.contractAgreementId === contractAgreementId,
        ),
      ),
      filterNotNull(),
    );
  }

  private searchText$(): Observable<string> {
    return (value$(this.searchText) as Observable<string>).pipe(
      map((it) => (it ?? '').trim()),
      distinctUntilChanged(),
    );
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }
}
