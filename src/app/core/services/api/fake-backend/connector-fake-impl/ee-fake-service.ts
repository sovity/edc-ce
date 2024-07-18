import {ConnectorLimits} from '@sovity.de/edc-client';
import {contractAgreementPage} from './contract-agreement-fake-service';

export const connectorLimits = (): ConnectorLimits => ({
  numActiveConsumingContractAgreements: contractAgreementPage(
    {},
  ).contractAgreements.filter(
    (it) => it.direction === 'CONSUMING' && it.terminationStatus === 'ONGOING',
  ).length,
  maxActiveConsumingContractAgreements: 1,
});
