export const getUrl = (input: Request | string, baseUrl: string): string => {
  const url = new URL(typeof input === 'string' ? input : input.url);
  const urlNoQuery = url.origin + url.pathname;
  return urlNoQuery.startsWith(baseUrl)
    ? urlNoQuery.substring(baseUrl.length)
    : urlNoQuery;
};

export const getMethod = (init: RequestInit | undefined): string =>
  init?.method ?? 'GET';

export const getBody = (input: RequestInit | undefined): null | any => {
  const body = input?.body?.toString();
  return body ? JSON.parse(body) : null;
};

export const getQueryParams = (input: Request | string): URLSearchParams => {
  const url = new URL(typeof input === 'string' ? input : input.url);
  return url.searchParams;
};
