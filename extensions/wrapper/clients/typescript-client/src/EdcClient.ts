import {
    Configuration,
    ConfigurationParameters,
    EnterpriseEditionApi,
    Middleware,
    UIApi,
    UseCaseApi,
} from './generated';
import {AccessTokenFetcher} from './oauth2/AccessTokenFetcher';
import {AccessTokenInjector} from './oauth2/AccessTokenInjector';
import {AccessTokenStore} from './oauth2/AccessTokenStore';
import {OAuth2ClientCredentials} from './oauth2/OAuth2ClientCredentials';
import {OAuthMiddleware} from './oauth2/OAuthMiddleware';

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
    let middleware: Middleware[] = [];
    let headers: Record<string, string> = {};

    if (opts.oAuth2ClientCredentials) {
        middleware.push(buildOAuthMiddleware(opts.oAuth2ClientCredentials));
    }
    if (opts.managementApiKey) {
        headers = buildApiKeyHeader(opts.managementApiKey);
    }

    const config = new Configuration({
        basePath: opts.managementApiUrl,
        headers,
        credentials: 'same-origin',
        middleware,
        ...opts.configOverrides,
    });

    return {
        uiApi: new UIApi(config),
        useCaseApi: new UseCaseApi(config),
        enterpriseEditionApi: new EnterpriseEditionApi(config),
    };
}

function buildOAuthMiddleware(
    clientCredentials: OAuth2ClientCredentials,
): Middleware {
    const accessTokenFetcher = new AccessTokenFetcher(clientCredentials);
    const accessTokenStore = new AccessTokenStore(accessTokenFetcher);
    const accessTokenInjector = new AccessTokenInjector();

    return new OAuthMiddleware(accessTokenInjector, accessTokenStore).build();
}

function buildApiKeyHeader(key: string) {
    return {
        'X-Api-Key': key,
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
