import {Injectable, OnDestroy} from '@angular/core';
import {Subject} from 'rxjs';
import {map} from 'rxjs/operators';
import {Action, State, StateContext} from '@ngxs/store';
import {
  CatalogPageQuery,
  CnfFilter,
  CnfFilterAttribute,
} from '@sovity.de/broker-server-client';
import {BrokerServerApiService} from '../../../../core/services/api/broker-server-api.service';
import {Fetched} from '../../../../core/services/models/fetched';
import {associateAsObj} from '../../../../core/utils/object-utils';
import {BrokerCatalogMapper} from '../catalog-page/mapping/broker-catalog-mapper';
import {CatalogPageResultMapped} from '../catalog-page/mapping/catalog-page-result-mapped';
import {FilterBoxItem} from '../filter-box/filter-box-item';
import {
  FilterBoxModel,
  buildFilterBoxModelWithNewData,
} from '../filter-box/filter-box-model';
import {FilterBoxVisibleState} from '../filter-box/filter-box-visible-state';
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
  onReset(ctx: Ctx, action: CatalogPage.Reset) {
    let state = ctx.getState();
    state.fetchSubscription?.unsubscribe();
    state = DEFAULT_CATALOG_PAGE_STATE_MODEL;
    if (action.initialConnectorEndpoints?.length) {
      state = this._addFilterBoxes(state, [
        this._buildConnectorEndpointFilterBoxModel(
          action.initialConnectorEndpoints,
        ),
      ]);
    }
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
        map((data) =>
          this.brokerCatalogMapper.buildCatalogPageResultMapped(data),
        ),
        Fetched.wrap({failureMessage: 'Failed fetching data offers.'}),
      )
      .subscribe((fetchedData) => {
        let state = {...ctx.getState(), fetchedData};
        fetchedData.ifReady((data) => {
          state = this._withReadyCatalogResult(state, data);
        });
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
    state = this._updateFilterModelById(state, action.filterId, (model) => ({
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
    state = this._updateFilterModelById(state, action.filterId, (model) => ({
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
    const state: CatalogPageStateModel = ctx.getState();
    const item = action.item;
    if (item.type === 'SEARCH_TEXT') {
      // Reset the Search
      this.onUpdateSearchText(ctx, new CatalogPage.UpdateSearchText(''));
    } else if (item.type === 'SELECTED_FILTER_ITEM') {
      // Remove the selected filter option
      const filterId = item.selectedFilterId!;
      const itemId = item.selectedFilterItem!.id;
      const selectedItems = state.filters[filterId].model.selectedItems;

      this.onUpdateFilterSelectedItems(
        ctx,
        new CatalogPage.UpdateFilterSelectedItems(
          filterId,
          selectedItems.filter((it) => it.id !== itemId),
        ),
      );
    }
  }

  private _buildConnectorEndpointFilterBoxModel(
    endpoints: string[],
  ): FilterBoxModel {
    const items: FilterBoxItem[] = endpoints.map((x) => ({
      type: 'ITEM',
      id: x,
      label: x,
    }));
    return {
      id: 'connectorEndpoint',
      title: 'Connector',
      selectedItems: items,
      availableItems: items,
      searchText: '',
    };
  }

  private _addFilterBoxes(
    state: CatalogPageStateModel,
    filterBoxes: FilterBoxModel[],
  ): CatalogPageStateModel {
    return this._recalculateActiveFilterItems({
      ...state,
      filters: {
        ...state.filters,
        ...associateAsObj(
          filterBoxes,
          (x) => x.id,
          (x) => FilterBoxVisibleState.buildVisibleState(x),
        ),
      },
    });
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
      filter: FilterBoxVisibleState,
      item: FilterBoxItem,
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

  private _updateFilterModelById(
    state: CatalogPageStateModel,
    filterId: string,
    patcher: (filter: FilterBoxModel) => FilterBoxModel,
  ): CatalogPageStateModel {
    const newModel = patcher(state.filters[filterId].model);
    return {
      ...state,
      filters: {
        ...state.filters,
        [filterId]: FilterBoxVisibleState.buildVisibleState(newModel),
      },
    };
  }

  private _withReadyCatalogResult(
    state: CatalogPageStateModel,
    data: CatalogPageResultMapped,
  ): CatalogPageStateModel {
    const filters = this.buildFiltersWithNewData(
      data.availableFilters,
      state.filters,
    );

    return {
      ...state,
      isPageReady: true,
      paginationDisabled: false,
      paginationMetadata: data.paginationMetadata,
      sortings: data.availableSortings,
      activeSorting: state.activeSorting ?? data.availableSortings[0] ?? null,
      filters,
    };
  }

  private buildFiltersWithNewData(
    cnfFilter: CnfFilter,
    oldFilter: Record<string, FilterBoxVisibleState>,
  ): Record<string, FilterBoxVisibleState> {
    return associateAsObj(
      cnfFilter.fields,
      (it: CnfFilterAttribute) => it.id,
      (it: CnfFilterAttribute) => {
        const old = oldFilter[it.id]?.model ?? null;
        const updated = buildFilterBoxModelWithNewData(it, old);
        return FilterBoxVisibleState.buildVisibleState(updated);
      },
    );
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
