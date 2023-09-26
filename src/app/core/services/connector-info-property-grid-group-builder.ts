import {Inject, Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {JsonDialogComponent} from '../../component-library/json-dialog/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../component-library/json-dialog/json-dialog/json-dialog.data';
import {PropertyGridGroup} from '../../component-library/property-grid/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../component-library/property-grid/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../component-library/property-grid/property-grid/property-grid-field.service';
import {APP_CONFIG, AppConfig} from '../config/app-config';
import {LastCommitInfo} from './api/model/last-commit-info';
import {Fetched} from './models/fetched';

@Injectable({providedIn: 'root'})
export class ConnectorInfoPropertyGridGroupBuilder {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private propertyGridUtils: PropertyGridFieldService,
    private matDialog: MatDialog,
  ) {}

  private onShowConnectorVersionClick(title: string, versionData: any) {
    const data: JsonDialogData = {
      title,
      subtitle: 'Show Details',
      icon: 'link',
      objectForJson: versionData,
    };
    this.matDialog.open(JsonDialogComponent, {data});
  }

  private asDate(dateString?: string) {
    return dateString ? new Date(dateString!).toLocaleString() : '';
  }

  getBackendVersionFields(
    lastCommitInfo: Fetched<LastCommitInfo>,
  ): PropertyGridField[] {
    return lastCommitInfo.match({
      ifOk: (lastCommitInfo) => [
        {
          icon: 'link',
          label: 'Jar Version',
          text: lastCommitInfo.jarBuildDate
            ? this.asDate(lastCommitInfo.jarBuildDate)
            : 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              'Jar Last Commit Information': lastCommitInfo.jarLastCommitInfo,
            }),
        },
        {
          icon: 'link',
          label: 'Environment Version',
          text: lastCommitInfo.envBuildDate
            ? this.asDate(lastCommitInfo.envBuildDate)
            : 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              'Environment Last Commit Information':
                lastCommitInfo.envLastCommitInfo,
            }),
        },
      ],
      ifError: (error) => [
        {
          icon: 'link',
          label: 'Jar Version',
          text: 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              Error: error.failureMessage,
            }),
        },
        {
          icon: 'link',
          label: 'Environment Version',
          text: 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              Error: error.failureMessage,
            }),
        },
      ],
      ifLoading: () => [
        {
          icon: 'link',
          label: 'Jar Version',
          text: 'Loading...',
        },
        {
          icon: 'link',
          label: 'Environment Version',
          text: 'Loading...',
        },
      ],
    });
  }

  getUiVersionField(
    uiBuildDate: Fetched<string>,
    uiCommitDetails: Fetched<string>,
  ): PropertyGridField[] {
    return uiBuildDate.match({
      ifOk: (data) => [
        {
          icon: 'link',
          label: 'UI Version',
          text: data.trim().toString()
            ? this.asDate(data.trim().toString())
            : 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              'UI Last Commit Information': uiCommitDetails.match({
                ifOk: (uiCommitdata) => uiCommitdata,
                ifError: (error) => error.failureMessage,
                ifLoading: () => 'Still Loading...',
              }),
            }),
        },
      ],
      ifError: (error) => [
        {
          icon: 'link',
          label: 'UI Version',
          text: 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              'UI Commit Information': error.failureMessage,
            }),
        },
      ],
      ifLoading: () => [
        {
          icon: 'link',
          label: 'UI Version',
          text: 'Loading...',
        },
      ],
    });
  }

  buildConnectorPropertyGridGroup(
    groupLabel: string | null,
  ): PropertyGridGroup {
    const config = this.config;

    const fields: PropertyGridField[] = [
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

    return {
      groupLabel,
      properties: fields,
    };
  }

  buildConnectorVersionGroup(
    lastCommitInfo: Fetched<LastCommitInfo>,
    uiBuildDate: Fetched<string>,
    uiCommitDetails: Fetched<string>,
  ): PropertyGridGroup {
    return {
      groupLabel: 'Version Information',
      properties: [
        ...this.getBackendVersionFields(lastCommitInfo),
        ...this.getUiVersionField(uiBuildDate, uiCommitDetails),
      ],
    };
  }

  buildPropertyGridGroups(
    lastCommitInformation: Fetched<LastCommitInfo>,
    UiBuildDate: Fetched<string>,
    UiCommitDetails: Fetched<string>,
  ): PropertyGridGroup[] {
    let fieldGroups: PropertyGridGroup[];

    fieldGroups = [
      this.buildConnectorPropertyGridGroup(null),
      this.buildConnectorVersionGroup(
        lastCommitInformation,
        UiBuildDate,
        UiCommitDetails,
      ),
    ];

    return fieldGroups.filter((it) => it.properties.length);
  }
}
