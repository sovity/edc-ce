/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Injectable} from '@angular/core';
import {HttpDatasourceQueryParamFormValue} from '../../shared/business/edit-asset-form/form/model/http-datasource-query-param-form-model';
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
