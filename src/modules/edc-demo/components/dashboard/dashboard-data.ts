import {Fetched} from '../../models/fetched';
import {DonutChartData} from '../dashboard-donut-chart/donut-chart-data';

export interface DashboardData {
  incomingTransfersChart: Fetched<DonutChartData>;
  outgoingTransfersChart: Fetched<DonutChartData>;
  numContractAgreements: Fetched<number>;
  numAssets: Fetched<number>;
  numCatalogEntries: Fetched<number>;
  numContractDefinitions: Fetched<number>;
  numPolicies: Fetched<number>;
  numCatalogs: Fetched<number>;
  connectorUrl: string;
  connectorOrganization: string;
}

export function defaultDashboardData(): DashboardData {
  return {
    incomingTransfersChart: Fetched.empty(),
    outgoingTransfersChart: Fetched.empty(),
    numContractAgreements: Fetched.empty(),
    numAssets: Fetched.empty(),
    numCatalogEntries: Fetched.empty(),
    numPolicies: Fetched.empty(),
    numContractDefinitions: Fetched.empty(),
    numCatalogs: Fetched.empty(),
    connectorUrl: '',
    connectorOrganization: '',
  };
}
