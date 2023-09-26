export interface HttpDataAddressParams {
  /**
   * (Base) URL of the request
   */
  baseUrl: string;

  /**
   * Http-method
   */
  method: string | null;

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
  queryParams: string | null;
  proxyMethod: boolean;
  proxyPath: boolean;
  proxyQueryParams: boolean;
  proxyBody: boolean;
}
