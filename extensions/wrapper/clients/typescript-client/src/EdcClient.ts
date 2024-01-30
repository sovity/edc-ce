import {OAuth2Client, OAuth2Fetch} from '@badgateway/oauth2-client';
import {
    Configuration,
    ConfigurationParameters,
    EnterpriseEditionApi,
    UIApi,
    UseCaseApi,
} from './generated';
import {OAuth2ClientCredentials} from './oauth2/OAuth2ClientCredentials';

/**
 * API Client for our sovity EDC
 */
export interface EdcClient {
    uiApi: UIApi;
    useCaseApi: UseCaseApi;
    enterpriseEditionApi: EnterpriseEditionApi;
}

/**
 * Configure & Build new EDC Client
 * @param opts opts
 */
export function buildEdcClient(opts: EdcClientOptions): EdcClient {
    let fetchWrapper: OAuth2Fetch | null = null;
    if (opts.oAuth2ClientCredentials) {
        const client = new OAuth2Client({
            server: opts.oAuth2ClientCredentials.serverUrl,
            tokenEndpoint: opts.oAuth2ClientCredentials.tokenEndpoint,
            clientId: opts.oAuth2ClientCredentials.clientId,
            clientSecret: opts.oAuth2ClientCredentials.clientSecret,
        });
        // Wrapper for fetch that automatically sets Authorization: Bearer headers and refreshes the token when needed
        fetchWrapper = new OAuth2Fetch({
            client: client,
            getNewToken: async () => {
                return client.clientCredentials();
            },
        });
    }

    const config = new Configuration({
        basePath: opts.managementApiUrl,
        headers: {
            'X-Api-Key': opts.managementApiKey ?? 'ApiKeyDefaultValue',
        },
        credentials: 'same-origin',
        fetchApi: fetchWrapper ? fetchWrapper.fetch : undefined,
        ...opts.configOverrides,
    });

    return {
        uiApi: new UIApi(config),
        useCaseApi: new UseCaseApi(config),
        enterpriseEditionApi: new EnterpriseEditionApi(config),
    };
}

/**
 * Options for instantiating an EDC API Client
 */
export interface EdcClientOptions {
    managementApiUrl: string;
    managementApiKey?: string;
    oAuth2ClientCredentials?: OAuth2ClientCredentials;
    configOverrides?: Partial<ConfigurationParameters>;
}
