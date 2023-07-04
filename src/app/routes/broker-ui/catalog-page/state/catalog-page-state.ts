import {Injectable, OnDestroy} from '@angular/core';
import {Subject} from 'rxjs';
import {Action, State, StateContext} from '@ngxs/store';
import {
  CatalogPageQuery,
  CatalogPageResult,
} from '@sovity.de/broker-server-client';
import {BrokerServerApiService} from '../../../../core/services/api/broker-server-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {BrokerCatalogMapper} from '../catalog-page/mapping/broker-catalog-mapper';
import {
  FilterValueSelectItem,
  mapCnfFilterItems,
} from '../filter-value-select/filter-value-select-item';
import {FilterValueSelectModel} from '../filter-value-select/filter-value-select-model';
import {FilterValueSelectVisibleState} from '../filter-value-select/filter-value-select-visible-state';
import {CatalogActiveFilterPill} from './catalog-active-filter-pill';
import {CatalogPage} from './catalog-page-actions';
import {
  CatalogPageStateModel,
  DEFAULT_CATALOG_PAGE_STATE_MODEL,
} from './catalog-page-state-model';
import {NgxsUtils} from './ngxs-utils';

type Ctx = StateContext<CatalogPageStateModel>;

@State<CatalogPageStateModel>({
  name: 'CatalogPageState',
  defaults: DEFAULT_CATALOG_PAGE_STATE_MODEL,
})
@Injectable()
export class CatalogPageState implements OnDestroy {
  constructor(
    private brokerServerApiService: BrokerServerApiService,
    private brokerCatalogMapper: BrokerCatalogMapper,
    private ngxsUtils: NgxsUtils,
  ) {
    this.ngxsUtils.sampleTime(CatalogPage.NeedFetch, CatalogPage.Fetch, 200);
  }

  @Action(CatalogPage.Reset)
  onReset(ctx: Ctx) {
    let state = ctx.getState();
    state.fetchSubscription?.unsubscribe();
    state = DEFAULT_CATALOG_PAGE_STATE_MODEL;
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.Fetch)
  onFetch(ctx: Ctx) {
    ctx.getState().fetchSubscription?.unsubscribe();
    const query = this.buildCatalogPageQuery(ctx.getState());

    const fetchSubscription = this.brokerServerApiService
      .catalogPage(query)
      .pipe(
        Fetched.wrap({failureMessage: 'Failed fetching data offers.'}),
        Fetched.map((data) =>
          this.brokerCatalogMapper.buildUiCatalogPageResult(data),
        ),
      )
      .subscribe((fetchedData) => {
        let state = {...ctx.getState(), fetchedData};
        state = fetchedData.ifReadyElse(
          (data) => this._setData(state, data),
          state,
        );
        ctx.setState(state);
      });

    ctx.patchState({fetchSubscription});
  }

  @Action(CatalogPage.UpdatePage)
  onUpdatePage(ctx: Ctx, action: CatalogPage.UpdatePage) {
    let state = ctx.getState();
    state = {...state, pageZeroBased: action.pageZeroBased};
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateSearchText)
  onUpdateSearchText(ctx: Ctx, action: CatalogPage.UpdateSearchText) {
    let state = ctx.getState();
    state = {...state, searchText: action.searchText};
    state = this._recalculateActiveFilterItems(state);
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateSorting)
  onUpdateSorting(ctx: Ctx, action: CatalogPage.UpdateSorting) {
    let state = ctx.getState();
    state = {...state, activeSorting: action.sorting};
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateFilterSelectedItems)
  onUpdateFilterSelectedItems(
    ctx: Ctx,
    action: CatalogPage.UpdateFilterSelectedItems,
  ) {
    let state = ctx.getState();
    state = this._updateFilter(state, action.filterId, (model) => ({
      ...model,
      selectedItems: action.selectedItems,
    }));
    state = this._recalculateActiveFilterItems(state);
    state = this._resetPage(state);
    ctx.setState(state);
    ctx.dispatch(CatalogPage.NeedFetch);
  }

  @Action(CatalogPage.UpdateFilterSearchText)
  onUpdateFilterSearchText(
    ctx: Ctx,
    action: CatalogPage.UpdateFilterSearchText,
  ) {
    let state = ctx.getState();
    state = this._updateFilter(state, action.filterId, (model) => ({
      ...model,
      searchText: action.searchText,
    }));
    ctx.setState(state);
  }

  @Action(CatalogPage.RemoveActiveFilterItem)
  onRemoveActiveFilterItem(
    ctx: Ctx,
    action: CatalogPage.RemoveActiveFilterItem,
  ) {
    let state: CatalogPageStateModel = ctx.getState();
    let item = action.item;
    if (item.type === 'SEARCH_TEXT') {
      // Reset the Search
      this.onUpdateSearchText(ctx, new CatalogPage.UpdateSearchText(''));
    } else if (item.type === 'SELECTED_FILTER_ITEM') {
      // Remove the selected filter option
      let filterId = item.selectedFilterId!;
      let itemId = item.selectedFilterItem!.id;
      let selectedItems = state.filters[filterId].model.selectedItems;

      this.onUpdateFilterSelectedItems(
        ctx,
        new CatalogPage.UpdateFilterSelectedItems(
          filterId,
          selectedItems.filter((it) => it.id !== itemId),
        ),
      );
    }
  }

  private _resetPage(state: CatalogPageStateModel): CatalogPageStateModel {
    return {...state, pageZeroBased: 0, paginationDisabled: true};
  }

  private _recalculateActiveFilterItems(
    state: CatalogPageStateModel,
  ): CatalogPageStateModel {
    const activeFilterItems: CatalogActiveFilterPill[] = [];
    if (state.searchText?.trim()) {
      activeFilterItems.push({
        type: 'SEARCH_TEXT',
        label: 'Search',
        value: state.searchText,
      });
    }

    const buildFilterItem = (
      filter: FilterValueSelectVisibleState,
      item: FilterValueSelectItem,
    ): CatalogActiveFilterPill => ({
      type: 'SELECTED_FILTER_ITEM',
      label: filter.model.title,
      value: item.label,
      selectedFilterId: filter.model.id,
      selectedFilterItem: item,
    });

    Object.values(state.filters)
      .flatMap((filter) =>
        filter.model.selectedItems.map((item) => buildFilterItem(filter, item)),
      )
      .forEach((item) => activeFilterItems.push(item));

    return {
      ...state,
      activeFilterPills: activeFilterItems,
    };
  }

  private _updateFilter(
    state: CatalogPageStateModel,
    id: string,
    patcher: (filter: FilterValueSelectModel) => FilterValueSelectModel,
  ): CatalogPageStateModel {
    const newModel = patcher(state.filters[id].model);
    return {
      ...state,
      filters: {
        ...state.filters,
        [id]: FilterValueSelectVisibleState.buildVisibleState(newModel),
      },
    };
  }

  private _setData(
    state: CatalogPageStateModel,
    data: CatalogPageResult,
  ): CatalogPageStateModel {
    const filters = data.availableFilters.fields.map(
      (filter): FilterValueSelectVisibleState => {
        let availableItems = mapCnfFilterItems(filter.values);
        let existingFilter = state.filters[filter.id];
        return FilterValueSelectVisibleState.buildVisibleState({
          id: filter.id,
          title: filter.title,
          availableItems,
          searchText: existingFilter?.model?.searchText ?? '',
          selectedItems: existingFilter?.model?.selectedItems ?? [],
        });
      },
    );

    return {
      ...state,
      isPageReady: true,
      paginationDisabled: false,
      paginationMetadata: data.paginationMetadata,
      sortings: data.availableSortings,
      activeSorting: state.activeSorting ?? data.availableSortings[0] ?? null,
      filters: Object.fromEntries(filters.map((it) => [it.model.id, it])),
    };
  }

  private buildCatalogPageQuery(state: CatalogPageStateModel) {
    const query: CatalogPageQuery = {
      searchQuery: state.searchText,
      filter: {
        selectedAttributeValues: Object.values(state.filters)
          .map((filter) => ({
            id: filter.model.id,
            selectedIds: [...filter.selectedIds],
          }))
          .filter((it) => it.selectedIds.length),
      },
      sorting: state.activeSorting?.sorting,
      pageOneBased: state.pageZeroBased + 1,
    };
    return query;
  }

  ngOnDestroy$ = new Subject<void>();

  ngOnDestroy() {
    this.ngOnDestroy$.next();
    this.ngOnDestroy$.complete();
  }
}
