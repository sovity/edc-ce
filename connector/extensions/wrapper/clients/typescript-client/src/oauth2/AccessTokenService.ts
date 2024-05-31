import {ClientCredentials} from './model/ClientCredentials';
import {
    ClientCredentialsResponse,
    fetchClientCredentials,
} from './utils/FetchUtils';

export class AccessTokenService {
    private activeRequest: Promise<ClientCredentialsResponse> | null = null;
    private refreshTimeout?: NodeJS.Timeout;
    private accessToken: string | null = null;

    constructor(private clientCredentials: ClientCredentials) {}

    async getAccessToken(): Promise<string> {
        if (!this.accessToken) {
            return await this.refreshAccessToken();
        }
        return this.accessToken;
    }

    /**
     * Synchronized refreshing of the access token
     */
    async refreshAccessToken(): Promise<string> {
        if (this.activeRequest) {
            await this.activeRequest;
            return this.accessToken!;
        }

        this.accessToken = null;
        this.activeRequest = fetchClientCredentials(this.clientCredentials);
        const response = await this.activeRequest;
        this.scheduleNextRefresh(response);
        this.accessToken = response.access_token;
        this.activeRequest = null;

        return this.accessToken;
    }

    private scheduleNextRefresh(response: ClientCredentialsResponse) {
        clearTimeout(this.refreshTimeout);
        const ms = (response.expires_in - 2) * 1000;
        this.refreshTimeout = setTimeout(() => {
            this.accessToken = null;
        }, ms);
    }
}
