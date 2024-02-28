import {Component, HostBinding, OnDestroy, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {PageEvent} from '@angular/material/paginator';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {BehaviorSubject, Subject} from 'rxjs';
import {filter, map, takeUntil} from 'rxjs/operators';
import {Store} from '@ngxs/store';
import {CatalogPageSortingItem} from '@sovity.de/broker-server-client';
import {LocalStoredValue} from 'src/app/core/utils/local-stored-value';
import {AssetDetailDialogDataService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog-data.service';
import {AssetDetailDialogService} from '../../../../component-library/catalog/asset-detail-dialog/asset-detail-dialog.service';
import {
  ViewModeEnum,
  isViewMode,
} from '../../../../component-library/catalog/view-selection/view-mode-enum';
import {BrokerServerApiService} from '../../../../core/services/api/broker-server-api.service';
import {FilterBoxItem} from '../filter-box/filter-box-item';
import {FilterBoxVisibleState} from '../filter-box/filter-box-visible-state';
import {CatalogActiveFilterPill} from '../state/catalog-active-filter-pill';
import {CatalogPage} from '../state/catalog-page-actions';
import {CatalogPageState} from '../state/catalog-page-state';
import {CatalogPageStateModel} from '../state/catalog-page-state-model';
import {CatalogDataOfferMapped} from './mapping/catalog-page-result-mapped';

@Component({
  selector: 'catalog-page',
  templateUrl: './catalog-page.component.html',
})
export class CatalogPageComponent implements OnInit, OnDestroy {
  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  @HostBinding('class.p-[20px]')
  @HostBinding('class.space-x-[20px]')
  cls = true;
  state!: CatalogPageStateModel;
  searchText = new FormControl('');
  sortBy = new FormControl<CatalogPageSortingItem | null>(null);
  viewModeEnum = ViewModeEnum;
  viewMode = new LocalStoredValue<ViewModeEnum>(
    ViewModeEnum.GRID,
    'brokerui.viewMode',
    isViewMode,
  );
  private fetch$ = new BehaviorSubject(null);

  // only tracked to prevent the component from resetting
  expandedFilterId = '';

  constructor(
    private assetDetailDialogDataService: AssetDetailDialogDataService,
    private assetDetailDialogService: AssetDetailDialogService,
    private brokerServerApiService: BrokerServerApiService,
    private store: Store,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.initializePage();
    this.startListeningToStore();
    this.startEmittingSearchText();
    this.startEmittingSortBy();
  }

  private initializePage() {
    const endpoints = this.parseConnectorEndpoints(
      this.route.snapshot.queryParams,
    );
    this.store.dispatch(new CatalogPage.Reset(endpoints));

    if (endpoints.length) {
      this.expandedFilterId = 'connectorEndpoint';
      // remove query params from url
      this.router.navigate([]);
    }
  }

  private startListeningToStore() {
    this.store
      .select<CatalogPageStateModel>(CatalogPageState)
      .pipe(takeUntil(this.ngOnDestroy$))
      .subscribe((state) => {
        this.state = state;
        if (this.searchText.value != state.searchText) {
          this.searchText.setValue(state.searchText);
        }
        if (this.sortBy.value?.sorting !== state.activeSorting?.sorting) {
          this.sortBy.setValue(state.activeSorting);
        }
        if (!this.expandedFilterId && this.state.fetchedData.isReady) {
          this.expandedFilterId =
            this.state.fetchedData.data.availableFilters.fields[0].id;
        }
      });
  }

  private startEmittingSearchText() {
    this.searchText.valueChanges
      .pipe(map((value) => value ?? ''))
      .subscribe((searchText) => {
        if (searchText != this.state.searchText) {
          this.store.dispatch(new CatalogPage.UpdateSearchText(searchText));
        }
      });
  }

  private startEmittingSortBy() {
    this.sortBy.valueChanges
      .pipe(map((value) => value ?? null))
      .subscribe((value) => {
        if (value?.sorting !== this.state.activeSorting?.sorting) {
          this.store.dispatch(new CatalogPage.UpdateSorting(value));
        }
      });
  }

  private parseConnectorEndpoints(params: Params): string[] {
    if (!('connectorEndpoint' in params)) {
      return [];
    }
    const endpoints = params.connectorEndpoint;
    return Array.isArray(endpoints) ? [...new Set(endpoints)] : [endpoints];
  }

  onDataOfferClick(dataOffer: CatalogDataOfferMapped) {
    const data =
      this.assetDetailDialogDataService.brokerDataOfferDetails(dataOffer);

    // Call the detail dialog endpoint so the view count is increased
    this.brokerServerApiService
      .dataOfferDetailPage({
        assetId: dataOffer.assetId,
        connectorEndpoint: dataOffer.connectorEndpoint,
      })
      .subscribe();

    this.assetDetailDialogService
      .open(data, this.ngOnDestroy$)
      .pipe(filter((it) => !!it?.refreshList))
      .subscribe(() => this.fetch$.next(null));
  }

  ngOnDestroy$ = new Subject();

  ngOnDestroy() {
    this.ngOnDestroy$.next(null);
    this.ngOnDestroy$.complete();
  }

  onSelectedItemsChange(
    filter: FilterBoxVisibleState,
    newSelectedItems: FilterBoxItem[],
  ) {
    this.store.dispatch(
      new CatalogPage.UpdateFilterSelectedItems(
        filter.model.id,
        newSelectedItems,
      ),
    );
  }

  onSearchTextChange(filter: FilterBoxVisibleState, newSearchText: string) {
    this.store.dispatch(
      new CatalogPage.UpdateFilterSearchText(filter.model.id, newSearchText),
    );
  }

  onRemoveActiveFilterItem(item: CatalogActiveFilterPill) {
    this.store.dispatch(new CatalogPage.RemoveActiveFilterItem(item));
  }

  onPageChange(event: PageEvent) {
    this.store.dispatch(new CatalogPage.UpdatePage(event.pageIndex));
  }

  onExpandedFilterChange(filterId: string, expanded: boolean) {
    if (expanded) {
      this.expandedFilterId = filterId;
    }
  }
}
