import {Injectable} from '@angular/core';
import {AppConfigService} from '../../../app/config/app-config.service';
import {PropertyGridField} from '../property-grid/property-grid-field';
import {PropertyGridUtils} from '../property-grid/property-grid-utils';

@Injectable()
export class ConnectorInfoPropertyGridBuilder {
  constructor(
    private appConfigService: AppConfigService,
    private propertyGridUtils: PropertyGridUtils,
  ) {}

  buildPropertyGrid(): PropertyGridField[] {
    const config = this.appConfigService.config;

    return [
      {
        icon: 'link',
        label: 'Connector Endpoint',
        ...this.propertyGridUtils.guessValue(config.connectorEndpoint),
      },
      {
        icon: 'vpn_key',
        label: 'DAPS Token URL',
        ...this.propertyGridUtils.guessValue(config.dapsOauthTokenUrl),
      },
      {
        icon: 'lock',
        label: 'DAPS JWKS URL',
        ...this.propertyGridUtils.guessValue(config.dapsOauthJwksUrl),
      },
      {
        icon: 'category',
        label: 'Connector ID',
        ...this.propertyGridUtils.guessValue(config.connectorId),
      },
      {
        icon: 'category',
        label: 'Connector Name',
        ...this.propertyGridUtils.guessValue(config.connectorName),
      },
      {
        icon: 'category',
        label: 'Connector IDS ID',
        ...this.propertyGridUtils.guessValue(config.connectorIdsId),
      },
      {
        icon: 'title',
        label: 'Title',
        ...this.propertyGridUtils.guessValue(config.connectorIdsTitle),
      },
      {
        icon: 'apartment',
        label: 'Curator Organization Name',
        ...this.propertyGridUtils.guessValue(config.curatorOrganizationName),
      },
      {
        icon: 'apartment',
        label: 'Curator URL',
        ...this.propertyGridUtils.guessValue(config.curatorUrl),
      },
      {
        icon: 'title',
        label: 'Description',
        ...this.propertyGridUtils.guessValue(config.connectorIdsDescription),
      },
      {
        icon: 'contact_support',
        label: 'Maintainer Organization Name',
        ...this.propertyGridUtils.guessValue(config.maintainerOrganizationName),
      },
      {
        icon: 'contact_support',
        label: 'Maintainer URL',
        ...this.propertyGridUtils.guessValue(config.maintainerUrl),
      },
    ];
  }
}
