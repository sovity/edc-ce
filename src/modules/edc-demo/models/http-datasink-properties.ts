export interface HttpDatasinkProperties {
  /**
   * URL the request is sent to
   */
  url: string;

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
}
