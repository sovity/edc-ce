/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';
import {Observable, combineLatest} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {EdcApiService} from '../../../../core/services/api/edc-api.service';
import {ConnectorEndpointUrlMapper} from '../../../../core/services/connector-endpoint-url-mapper';
import {DataOffer} from '../../../../core/services/models/data-offer';
import {Fetched} from '../../../../core/services/models/fetched';
import {MultiFetched} from '../../../../core/services/models/multi-fetched';
import {assetSearchTargets, search} from '../../../../core/utils/search-utils';
import {CatalogApiUrlService} from './catalog-api-url.service';
import {
  CatalogBrowserPageData,
  ContractOfferRequest,
} from './catalog-browser-page.data';
import {DataOfferBuilder} from './data-offer-builder';

@Injectable()
export class CatalogBrowserPageService {
  constructor(
    private edcApiService: EdcApiService,
    private catalogApiUrlService: CatalogApiUrlService,
    private dataOfferBuilder: DataOfferBuilder,
    private connectorEndpointUrlMapper: ConnectorEndpointUrlMapper,
  ) {}

  contractOfferPageData$(
    refresh$: Observable<any>,
    searchText$: Observable<string>,
  ): Observable<CatalogBrowserPageData> {
    return combineLatest([
      refresh$.pipe(switchMap(() => this.fetchCatalogs())),
      searchText$,
    ]).pipe(
      map(([data, searchText]): CatalogBrowserPageData => {
        // Merge fetch results
        const contractOffers = data.requestTotals.data.flat();
        // Apply filter
        const filteredContractOffers = this.filterContractOffers(
          contractOffers,
          searchText,
        );

        return {
          requests: data.requests,
          requestTotals: data.requestTotals,
          filteredDataOffers: filteredContractOffers,
          numTotalContractOffers: contractOffers.length,
        };
      }),
    );
  }

  filterContractOffers(
    dataOffers: DataOffer[],
    searchText: string,
  ): DataOffer[] {
    return search(dataOffers, searchText, (dataOffer) =>
      assetSearchTargets(dataOffer.asset),
    );
  }

  fetchCatalogs(): Observable<
    Pick<CatalogBrowserPageData, 'requests' | 'requestTotals'>
  > {
    // Prepare to fetch individual Catalogs
    const urls = this.catalogApiUrlService.getAllProviders();
    const sources = urls.map((it) =>
      this.fetchDataOffers(it).pipe(
        Fetched.wrap({failureMessage: 'Failed fetching catalog.'}),
      ),
    );

    return combineLatest(sources).pipe(
      map((results) => MultiFetched.aggregate(results)),
      map(
        (
          requestTotals: MultiFetched<DataOffer[]>,
        ): Pick<CatalogBrowserPageData, 'requests' | 'requestTotals'> => {
          const presetUrls = this.catalogApiUrlService.getPresetProviders();
          return {
            requestTotals,
            requests: requestTotals.results.map(
              (data, index): ContractOfferRequest => ({
                url: urls[index],
                isPresetUrl: presetUrls.includes(urls[index]),
                data,
              }),
            ),
          };
        },
      ),
    );
  }

  private fetchDataOffers(endpointAndParticipantId: string) {
    const {connectorEndpoint, participantId} =
      this.connectorEndpointUrlMapper.extractConnectorEndpointAndParticipantId(
        endpointAndParticipantId,
      );

    return this.edcApiService
      .getCatalogPageDataOffers(connectorEndpoint, participantId)
      .pipe(
        map((dataOffers) =>
          dataOffers.map((dataOffer) =>
            this.dataOfferBuilder.buildDataOffer(dataOffer),
          ),
        ),
      );
  }
}
