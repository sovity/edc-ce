import {Inject, Injectable} from '@angular/core';
import {PropertyGridField} from '../../component-library/property-grid/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../component-library/property-grid/property-grid/property-grid-field.service';
import {APP_CONFIG, AppConfig} from '../config/app-config';

@Injectable()
export class ConnectorInfoPropertyGridBuilder {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private propertyGridUtils: PropertyGridFieldService,
  ) {}

  buildPropertyGrid(): PropertyGridField[] {
    const config = this.config;

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
