import {AccessTokenFetcher} from './AccessTokenFetcher';

export class AccessTokenStore {
    _accessToken?: string;
    accessTokenFetcher: AccessTokenFetcher;
    private isRefreshing = false;
    private currentFetchPromise: Promise<string | undefined> =
        Promise.resolve(undefined);

    constructor(accessTokenFetcher: AccessTokenFetcher, token?: string) {
        this.accessToken = token;
        this.accessTokenFetcher = accessTokenFetcher;
    }

    get accessToken(): string | undefined {
        return this._accessToken;
    }

    set accessToken(token: string | undefined) {
        this._accessToken = token;
    }

    // Synchronized refreshing of the access token
    async refreshToken() {
        if (this.isRefreshing) {
            await this.currentFetchPromise;
            return;
        }

        this.isRefreshing = true;
        this.currentFetchPromise = this.accessTokenFetcher.fetch();
        const newToken = await this.currentFetchPromise;
        if (newToken) {
            this.accessToken = newToken;
        }
        this.isRefreshing = false;
    }
}
