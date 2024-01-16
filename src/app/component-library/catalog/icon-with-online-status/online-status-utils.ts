import {
  CatalogDataOfferConnectorOnlineStatusEnum,
  ConnectorDetailPageResultOnlineStatusEnum,
  ConnectorListEntryOnlineStatusEnum,
  DataOfferDetailPageResultConnectorOnlineStatusEnum,
} from '@sovity.de/broker-server-client';

export type ConnectorOnlineStatus =
  | CatalogDataOfferConnectorOnlineStatusEnum
  | ConnectorListEntryOnlineStatusEnum
  | DataOfferDetailPageResultConnectorOnlineStatusEnum
  | ConnectorDetailPageResultOnlineStatusEnum;

export function getOnlineStatusColor(status: ConnectorOnlineStatus): string {
  switch (status) {
    case 'ONLINE':
      return 'broker-online-status-online';
    case 'OFFLINE':
      return 'broker-online-status-offline';
    case 'DEAD':
      return 'broker-online-status-dead';
    default:
      return '';
  }
}

export function getOnlineStatusIcon(status: ConnectorOnlineStatus): string {
  switch (status) {
    case 'ONLINE':
      return 'cloud_done';
    case 'OFFLINE':
      return 'pause_circle';
    case 'DEAD':
      return 'remove_circle';
    default:
      return '';
  }
}

export function getOnlineStatusSmallIcon(
  status: ConnectorOnlineStatus,
): string {
  switch (status) {
    case 'ONLINE':
      return 'cloud_done';
    case 'OFFLINE':
      return 'pause_circle';
    case 'DEAD':
      return 'remove_circle';
    default:
      return '';
  }
}
