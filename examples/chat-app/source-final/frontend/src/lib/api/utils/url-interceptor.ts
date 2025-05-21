/*
 * Copyright 2025 sovity GmbH
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
 */
/**
 * Collects URLs + Method + ResponseFn and then matches them in order.
 *
 * This class only exists to clean up the fake-backend code.
 */
export class UrlInterceptor {
  private entries: {
    urlPattern: string;
    method: string;
    response: ResponseFn;
  }[] = [];

  private lastUrlPattern: string | null = null;

  constructor(
    public requestUrl: string,
    public requestMethod: string,
  ) {}

  url(urlPattern: string): this {
    this.lastUrlPattern = urlPattern;
    return this;
  }

  on(method: string, response: ResponseFn): this {
    const urlPattern = this.lastUrlPattern;
    if (!urlPattern) {
      throw new Error('Call .url() before calling .on()');
    }
    this.entries.push({urlPattern, method, response});
    return this;
  }

  async tryMatch(): Promise<Response> {
    for (const entry of this.entries) {
      if (entry.method !== this.requestMethod) {
        continue;
      }

      const regexp = '^' + entry.urlPattern.replace(/\*/g, '(.*)') + '$';
      const match = this.requestUrl.match(regexp);
      if (!match) {
        continue;
      }

      const urlMatch = match
        .filter((_, index) => index > 0)
        .map((pathSegment) => decodeURIComponent(pathSegment));

      return await entry.response(...urlMatch);
    }

    console.warn(
      `Unmatched request: ${this.requestMethod} ${this.requestUrl}`,
      this.entries.map((it) => `${it.method} ${it.urlPattern}`),
    );

    return Promise.reject(
      new Error(`Unmatched request: ${this.requestMethod} ${this.requestUrl}`),
    );
  }
}

export type ResponseFn = (...match: string[]) => Promise<Response>;
