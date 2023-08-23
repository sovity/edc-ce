import {FetchAPI} from '@sovity.de/edc-client';
import {AssetCreateRequest} from '@sovity.de/edc-client/dist/generated/models/AssetCreateRequest';
import {assetPage, createAsset, deleteAsset} from './asset-fake-service';
import {getBody, getMethod, getUrl} from './utils/request-utils';
import {ok} from './utils/response-utils';
import {UrlInterceptor} from './utils/url-interceptor';

export const EDC_FAKE_BACKEND: FetchAPI = async (
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> => {
  let url = getUrl(input, 'http://edc.fake-backend/wrapper/ui/');
  let method = getMethod(init);
  let body = getBody(init);

  console.log(...['Fake Backend:', method, url, body].filter((it) => !!it));

  return new UrlInterceptor(url, method)

    .url('pages/asset-page')
    .on('GET', () => ok(assetPage()))

    .url('pages/asset-page/assets')
    .on('POST', () => ok(createAsset(body as AssetCreateRequest)))

    .url('pages/asset-page/assets/*')
    .on('DELETE', (assetId) => ok(deleteAsset(assetId)))

    .tryMatch();
};
