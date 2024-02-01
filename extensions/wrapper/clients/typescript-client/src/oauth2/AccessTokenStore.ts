import {AccessTokenFetcher} from './AccessTokenFetcher';

export class AccessTokenStore {
    private activeRequest: Promise<string> | null = null;

    constructor(
        private accessTokenFetcher: AccessTokenFetcher,
        public accessToken?: string,
    ) {}

    // Synchronized refreshing of the access token
    async refreshToken() {
        if (this.activeRequest) {
            await this.activeRequest;
            return;
        }

        this.activeRequest = this.accessTokenFetcher.fetch();
        const newToken = await this.activeRequest;
        if (newToken) {
            this.accessToken = newToken;
        }
        this.activeRequest = null;
    }
}
