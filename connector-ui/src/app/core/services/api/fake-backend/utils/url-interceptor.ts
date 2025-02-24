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

  constructor(public requestUrl: String, public requestMethod: string) {}

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
    for (let entry of this.entries) {
      if (entry.method !== this.requestMethod) {
        continue;
      }

      const regexp = '^' + entry.urlPattern.replace(/\*/g, '(.*)') + '$';
      let match = this.requestUrl.match(regexp);
      if (!match) {
        continue;
      }

      match = match
        .filter((_, index) => index > 0)
        .map((pathSegment) => decodeURIComponent(pathSegment));

      return await entry.response(...match);
    }

    console.warn(
      `Unmatched request: ${this.requestMethod} ${this.requestUrl}`,
      this.entries.map((it) => `${it.method} ${it.urlPattern}`),
    );

    return Promise.reject(
      `Unmatched request: ${this.requestMethod} ${this.requestUrl}`,
    );
  }
}

export type ResponseFn = (...match: string[]) => Promise<Response>;

export interface MethodMatcher {
  on(method: string, accept: ResponseFn): MethodMatcher;
}
