import {
    Configuration,
    ConfigurationParameters,
    EnterpriseEditionApi,
    Middleware,
    UIApi,
    UseCaseApi,
} from './generated';
import {buildOAuthMiddleware} from './oauth2/Middleware';
import {ClientCredentials} from './oauth2/model/ClientCredentials';

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

    if (opts.clientCredentials) {
        middleware.push(buildOAuthMiddleware(opts.clientCredentials));
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
    clientCredentials?: ClientCredentials;
    configOverrides?: Partial<ConfigurationParameters>;
}
