import {OAuth2ClientCredentials} from './OAuth2ClientCredentials';

export class AccessTokenFetcher {
    private readonly clientCredentials: OAuth2ClientCredentials;
    private readonly credentialsFormData: FormData;

    constructor(clientCredentials: OAuth2ClientCredentials) {
        this.clientCredentials = clientCredentials;
        this.credentialsFormData = this.buildFormData();
    }

    async fetch(): Promise<string | undefined> {
        try {
            let response = await fetch(this.clientCredentials.tokenUrl, {
                method: 'POST',
                body: this.credentialsFormData,
            });
            let json: any = await response.json();
            if (json && 'access_token' in json) {
                return json['access_token'];
            }
            return undefined;
        } catch (err) {
            return undefined;
        }
    }

    private buildFormData(): FormData {
        const formData = new FormData();
        formData.append('grant_type', 'client_credentials');
        formData.append('client_id', this.clientCredentials.clientId);
        formData.append('client_secret', this.clientCredentials.clientSecret);
        return formData;
    }
}
