/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Observable} from 'rxjs';
import {CatalogBrowserPageData} from '../catalog-browser-page/catalog-browser-page.data';

export interface CatalogBrowserFetchDetailDialogData {
  data$: Observable<CatalogBrowserPageData>;
  refresh: () => void;
}
