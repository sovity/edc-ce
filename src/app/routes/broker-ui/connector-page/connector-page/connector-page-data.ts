import {ConnectorPageResult} from '@sovity.de/edc-client';
import {Fetched} from '../../../../core/services/models/fetched';

export interface ConnectorPageData {
  fetchedConnectors: Fetched<ConnectorPageResult>;
}

export function emptyConnectorPageStateModel(): ConnectorPageData {
  return {
    fetchedConnectors: Fetched.empty(),
  };
}
