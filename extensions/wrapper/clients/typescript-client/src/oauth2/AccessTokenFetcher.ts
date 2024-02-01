import {createUrlEncodedParamsString} from './HttpUtils';
import {OAuth2ClientCredentials} from './OAuth2ClientCredentials';

export class AccessTokenFetcher {
    private readonly clientCredentials: OAuth2ClientCredentials;
    private readonly credentialsBody: string;

    constructor(clientCredentials: OAuth2ClientCredentials) {
        this.clientCredentials = clientCredentials;
        this.credentialsBody = this.buildFormData();
    }

    async fetch(): Promise<string> {
        let response = await fetch(this.clientCredentials.tokenUrl, {
            method: 'POST',
            body: this.credentialsBody,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });
        let json: any = await response.json();
        if (json && 'access_token' in json) {
            return json['access_token'];
        }
        throw new Error('access_token');
    }

    private buildFormData(): string {
        const formData = {
            grant_type: 'client_credentials',
            client_id: this.clientCredentials.clientId,
            client_secret: this.clientCredentials.clientSecret,
        };

        return createUrlEncodedParamsString(formData);
    }
}
