import {Injectable} from '@angular/core';
import {HttpDatasourceQueryParamFormValue} from 'src/app/component-library/edit-asset-form/edit-asset-form/form/model/http-datasource-query-param-form-model';
import {
  everythingAfter,
  everythingBefore,
  trimOrEmpty,
} from '../utils/string-utils';

@Injectable({providedIn: 'root'})
export class QueryParamsMapper {
  getBaseUrlWithoutQueryParams(rawUrl: string): string {
    return everythingBefore('?', trimOrEmpty(rawUrl));
  }

  /**
   * Merges query params from the base URL with the additional ones.
   */
  getFullQueryString(
    baseUrlWithQueryParams: string,
    additionalQueryParams: HttpDatasourceQueryParamFormValue[],
  ): string | null {
    const queryParamSegments = additionalQueryParams.map((param) =>
      this.encodeQueryParam(param),
    );

    return [
      everythingAfter('?', trimOrEmpty(baseUrlWithQueryParams)),
      ...queryParamSegments,
    ]
      .filter((it) => !!it)
      .join('&');
  }

  private encodeQueryParam(param: HttpDatasourceQueryParamFormValue): string {
    const k = encodeURIComponent(trimOrEmpty(param.paramName));
    const v = encodeURIComponent(trimOrEmpty(param.paramValue));
    return this.buildQueryParam(k, v);
  }

  private buildQueryParam(name: string, value: string) {
    return `${name}=${value}`;
  }
}
