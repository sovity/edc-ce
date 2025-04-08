/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
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
