export const getUrl = (input: Request | string, baseUrl: string): string => {
  let url = new URL(typeof input === 'string' ? input : input.url);
  let urlNoQuery = url.origin + url.pathname;
  return urlNoQuery.startsWith(baseUrl)
    ? urlNoQuery.substring(baseUrl.length)
    : urlNoQuery;
};

export const getMethod = (init: RequestInit | undefined): string =>
  init?.method ?? 'GET';

export const getBody = (input: RequestInit | undefined): null | any => {
  let body = input?.body?.toString();
  return body ? JSON.parse(body) : null;
};

export const getQueryParams = (input: Request | string): URLSearchParams => {
  let url = new URL(typeof input === 'string' ? input : input.url);
  return url.searchParams;
};
