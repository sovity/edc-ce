import {ClientCredentialsResponse, fetchClientCredentials} from './FetchUtils';
import {buildTokenRequestFormData} from './RequestUtils';
import {ClientCredentials} from './model/ClientCredentials';

export class AccessTokenService {
    private activeRequest: Promise<ClientCredentialsResponse> | null = null;
    private refreshTimeout?: NodeJS.Timeout;
    private tokenRequestFormData: string;

    constructor(
        private clientCredentials: ClientCredentials,
        private accessToken: string | null,
    ) {
        this.tokenRequestFormData = buildTokenRequestFormData(
            clientCredentials.clientId,
            clientCredentials.clientSecret,
        );
    }

    async getAccessToken(): Promise<string> {
        if (!this.accessToken) {
            await this.refreshAccessToken();
        }
        return this.accessToken!;
    }

    /**
     * Synchronized refreshing of the access token
     */
    async refreshAccessToken(): Promise<string> {
        console.log('Refreshing');
        if (this.activeRequest) {
            await this.activeRequest;
            return this.accessToken!;
        }

        this.accessToken = null;
        this.activeRequest = fetchClientCredentials(
            this.clientCredentials.tokenUrl,
            this.tokenRequestFormData,
        );
        const clientCredentialsResponse = await this.activeRequest;
        this.scheduleNextRefresh(clientCredentialsResponse);
        this.accessToken = clientCredentialsResponse.access_token;
        this.activeRequest = null;

        return this.accessToken;
    }

    private scheduleNextRefresh(
        clientCredentialsResponse: ClientCredentialsResponse,
    ) {
        clearTimeout(this.refreshTimeout);
        const ms = (clientCredentialsResponse.expires_in - 2) * 1000;
        this.refreshTimeout = setTimeout(() => this.refreshAccessToken(), ms);
    }
}
