/**
 * Credentials for connecting to the EDC via the OAuth2 "Client Credentials" flow.
 */
export interface ClientCredentials {
    tokenUrl: string;
    clientId: string;
    clientSecret: string;
}
