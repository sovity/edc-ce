import {Subscription} from 'rxjs';
import {
  CatalogPageSortingItem,
  PaginationMetadata,
} from '@sovity.de/broker-server-client';
import {Fetched} from '../../../../core/services/models/fetched';
import {BrokerCatalogPageResult} from '../catalog-page/mapping/broker-catalog-page-result';
import {FilterValueSelectVisibleState} from '../filter-value-select/filter-value-select-visible-state';
import {CatalogActiveFilterPill} from './catalog-active-filter-pill';

export interface CatalogPageStateModel {
  /**
   * We need an initial fetch to succeed so we can get the filters and sortings.
   */
  isPageReady: boolean;

  /**
   * User Input: Search Text
   */
  searchText: string;

  /**
   * User Input: Selected Filter
   */
  activeSorting: CatalogPageSortingItem | null;

  /**
   * User Input: Selected Filters
   * (and also includes filter definitions)
   */
  filters: Record<string, FilterValueSelectVisibleState>;

  /**
   * sorting definitions
   */
  sortings: CatalogPageSortingItem[];

  /**
   * Active Filter Pills as derived from user input.
   */
  activeFilterPills: CatalogActiveFilterPill[];

  /**
   * Fetch Subscription, so we can more fine-tuned decide when to cancel it
   */
  fetchSubscription: Subscription | null;

  /**
   * Data
   */
  fetchedData: Fetched<BrokerCatalogPageResult>;

  /**
   * Pagination Information kept also between calls so we can render the pagination component, always.
   */
  paginationMetadata: PaginationMetadata;

  /**
   * We disable the pagination component for calls after the filters have changed
   * because we expect different numbers of results.
   */
  paginationDisabled: boolean;

  /**
   * Current Page
   */
  pageZeroBased: number;
}

export const EMPTY_PAGINATION_METADATA: PaginationMetadata = {
  pageSize: 0,
  pageOneBased: 1,
  numTotal: 0,
  numVisible: 0,
};

export const DEFAULT_CATALOG_PAGE_STATE_MODEL: CatalogPageStateModel = {
  isPageReady: false,

  searchText: '',
  activeFilterPills: [],
  filters: {},
  sortings: [],
  activeSorting: null,

  fetchSubscription: null,
  fetchedData: Fetched.empty(),
  paginationMetadata: EMPTY_PAGINATION_METADATA,
  paginationDisabled: true,
  pageZeroBased: 0,
};
