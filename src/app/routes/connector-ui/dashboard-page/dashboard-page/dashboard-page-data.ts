import {PropertyGridGroup} from '../../../../component-library/property-grid/property-grid-group/property-grid-group';
import {Fetched} from '../../../../core/services/models/fetched';
import {DonutChartData} from '../dashboard-donut-chart/donut-chart-data';

export interface DashboardPageData {
  incomingTransfersChart: Fetched<DonutChartData>;
  outgoingTransfersChart: Fetched<DonutChartData>;
  numContractAgreements: Fetched<number>;
  numAssets: Fetched<number>;
  numCatalogEntries: Fetched<number>;
  numContractDefinitions: Fetched<number>;
  numPolicies: Fetched<number>;
  numCatalogs: Fetched<number>;
  connectorProperties: PropertyGridGroup[];
  connectorEndpoint: string;
  title: string;
  description: string;
}

export function defaultDashboardData(): DashboardPageData {
  return {
    incomingTransfersChart: Fetched.empty(),
    outgoingTransfersChart: Fetched.empty(),
    numContractAgreements: Fetched.empty(),
    numAssets: Fetched.empty(),
    numCatalogEntries: Fetched.empty(),
    numPolicies: Fetched.empty(),
    numContractDefinitions: Fetched.empty(),
    numCatalogs: Fetched.empty(),
    connectorProperties: [],
    connectorEndpoint: 'Loading...',
    title: 'Loading...',
    description: 'Loading...',
  };
}
