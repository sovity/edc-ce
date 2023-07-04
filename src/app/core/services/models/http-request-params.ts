export interface HttpRequestParams {
  /**
   * (Base) URL of the request
   */
  baseUrl: string;

  /**
   * If proxy path is set, this
   */

  defaultPath: string | null;

  /**
   * Http-method
   */
  method: string;

  /**
   * Header-Name ("Authorization"), where the secrets are passed into
   */
  authHeaderName: string | null;

  /**
   * Header-Value ("Bearer ...")
   */
  authHeaderValue: string | null;

  /**
   * Secret referencing API Key
   */
  authHeaderSecretName: string | null;

  /**
   * Additional headers to be sent
   */
  headers: Record<string, string>;

  /**
   * Query Parameters
   */
  queryParams: string;
  proxyMethod: boolean;
  proxyPath: boolean;
  proxyQueryParams: boolean;
  proxyBody: boolean;
}
