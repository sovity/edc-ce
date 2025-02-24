import {Observable} from 'rxjs';
import {CatalogBrowserPageData} from '../catalog-browser-page/catalog-browser-page.data';

export interface CatalogBrowserFetchDetailDialogData {
  data$: Observable<CatalogBrowserPageData>;
  refresh: () => void;
}
