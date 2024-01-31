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
    let oAuthMiddleware: Middleware[] | undefined;

    if (opts.oAuth2ClientCredentials) {
        const accessTokenFetcher = new AccessTokenFetcher(
            opts.oAuth2ClientCredentials,
        );
        const accessTokenStore = new AccessTokenStore(accessTokenFetcher);
        const accessTokenInjector = new AccessTokenInjector();

        const middleware = new OAuthMiddleware(
            accessTokenInjector,
            accessTokenStore,
        ).build();

        oAuthMiddleware = [middleware];
    }

    const config = new Configuration({
        basePath: opts.managementApiUrl,
        headers: {
            'X-Api-Key': opts.managementApiKey ?? 'ApiKeyDefaultValue',
        },
        credentials: 'same-origin',
        middleware: oAuthMiddleware ? oAuthMiddleware : undefined,
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
