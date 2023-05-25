import {
    BrokerServerApi,
    Configuration,
    ConfigurationParameters,
    EnterpriseEditionApi,
    UIApi,
    UseCaseApi,
} from './generated';

/**
 * API Client for our sovity EDC
 */
export interface EdcClient {
    uiApi: UIApi;
    useCaseApi: UseCaseApi;
    enterpriseEditionApi: EnterpriseEditionApi;
    brokerServerApi: BrokerServerApi;
}

/**
 * Configure & Build new EDC Client
 * @param opts opts
 */
export function buildEdcClient(opts: EdcClientOptions): EdcClient {
    const config = new Configuration({
        basePath: opts.managementApiUrl,
        headers: {
            'x-api-key': opts.managementApiKey ?? 'ApiKeyDefaultValue',
        },
        credentials: 'same-origin',
        ...opts.configOverrides,
    });

    return {
        uiApi: new UIApi(config),
        useCaseApi: new UseCaseApi(config),
        enterpriseEditionApi: new EnterpriseEditionApi(config),
        brokerServerApi: new BrokerServerApi(config),
    };
}

/**
 * Options for instantiating an EDC API Client
 */
export interface EdcClientOptions {
    managementApiUrl: string;
    managementApiKey?: string;
    configOverrides?: Partial<ConfigurationParameters>;
}
