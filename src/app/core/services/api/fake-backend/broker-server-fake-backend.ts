import {
  CatalogPageQueryFromJSON,
  CatalogPageResultToJSON,
  ConnectorDetailPageQueryFromJSON,
  ConnectorDetailPageResultToJSON,
  ConnectorPageQueryFromJSON,
  ConnectorPageResultToJSON,
  DataOfferDetailPageQueryFromJSON,
  DataOfferDetailPageResultToJSON,
} from '@sovity.de/broker-server-client';
import {FetchAPI} from '@sovity.de/edc-client';
import {
  getCatalogPage,
  getDataOfferDetailPage,
} from './broker-fake-impl/catalog-fake-impl';
import {
  getConnectorDetailPage,
  getConnectorPage,
} from './broker-fake-impl/connector-fake-impl';
import {
  getBody,
  getMethod,
  getQueryParams,
  getUrl,
} from './utils/request-utils';
import {ok} from './utils/response-utils';
import {UrlInterceptor} from './utils/url-interceptor';

export const BROKER_SERVER_FAKE_BACKEND: FetchAPI = async (
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> => {
  const url = getUrl(input, 'http://edc.fake-backend/wrapper/broker/');
  const method = getMethod(init);
  const body = getBody(init);
  const params = getQueryParams(input);

  console.log(
    ...[
      'Fake Backend:',
      method,
      url,
      (params as any)['size'] ? params : null,
      body,
    ].filter((it) => !!it),
  );

  return new UrlInterceptor(url, method)

    .url('catalog-page')
    .on('POST', () => {
      const query = CatalogPageQueryFromJSON(body);
      const result = getCatalogPage(query);
      return ok(CatalogPageResultToJSON(result));
    })

    .url('connector-page')
    .on('POST', () => {
      const query = ConnectorPageQueryFromJSON(body);
      const result = getConnectorPage(query);
      return ok(ConnectorPageResultToJSON(result));
    })

    .url('data-offer-detail-page')
    .on('POST', () => {
      const query = DataOfferDetailPageQueryFromJSON(body);
      const result = getDataOfferDetailPage(query);
      return ok(DataOfferDetailPageResultToJSON(result));
    })

    .url('connector-detail-page')
    .on('POST', () => {
      const query = ConnectorDetailPageQueryFromJSON(body);
      const result = getConnectorDetailPage(query);
      return ok(ConnectorDetailPageResultToJSON(result));
    })

    .tryMatch();
};
