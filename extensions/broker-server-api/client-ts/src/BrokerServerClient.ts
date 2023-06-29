import {
    BrokerServerApi,
    Configuration,
    ConfigurationParameters
} from './generated';

/**
 * API Client for our sovity Broker Server Client
 */
export interface BrokerServerClient {
    brokerServerApi: BrokerServerApi;
}

/**
 * Configure & Build new Broker Server Client
 * @param opts opts
 */
export function buildBrokerServerClient(opts: BrokerServerClientOptions): BrokerServerClient {
    const config = new Configuration({
        basePath: opts.managementApiUrl,
        headers: {
            'x-api-key': opts.managementApiKey ?? 'ApiKeyDefaultValue',
        },
        credentials: 'same-origin',
        ...opts.configOverrides,
    });

    return {
        brokerServerApi: new BrokerServerApi(config),
    };
}

/**
 * Options for instantiating an EDC API Client
 */
export interface BrokerServerClientOptions {
    managementApiUrl: string;
    managementApiKey?: string;
    configOverrides?: Partial<ConfigurationParameters>;
}
