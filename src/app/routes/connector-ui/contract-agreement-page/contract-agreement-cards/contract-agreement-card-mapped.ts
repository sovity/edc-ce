import {ContractAgreementCard} from '@sovity.de/edc-client';
import {Asset} from '../../../../core/services/models/asset';

export type ContractAgreementCardMapped = Omit<
  ContractAgreementCard,
  'asset'
> & {
  asset: Asset;
  isInProgress: boolean;
  isConsumingLimitsEnforced: boolean;
  statusText: string;
  statusTooltipText: string;
  canTransfer: boolean;
  searchTargets: (string | null)[];
};
