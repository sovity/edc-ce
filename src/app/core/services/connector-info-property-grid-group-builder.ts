import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {DashboardPage} from '@sovity.de/edc-client';
import {JsonDialogComponent} from '../../component-library/json-dialog/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../component-library/json-dialog/json-dialog/json-dialog.data';
import {PropertyGridGroup} from '../../component-library/property-grid/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../component-library/property-grid/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../component-library/property-grid/property-grid/property-grid-field.service';
import {LastCommitInfo} from './api/model/last-commit-info';
import {Fetched} from './models/fetched';
import {ParticipantIdLocalization} from './participant-id-localization';

@Injectable({providedIn: 'root'})
export class ConnectorInfoPropertyGridGroupBuilder {
  constructor(
    private participantIdLocalization: ParticipantIdLocalization,
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
    dashboardData: Fetched<DashboardPage>,
  ): PropertyGridGroup {
    const fields: PropertyGridField[] = dashboardData.match({
      ifLoading: () => [{icon: 'info', label: 'Loading', text: 'Loading...'}],
      ifError: () => [
        {
          icon: 'error',
          label: 'Error',
          text: 'Failed loading connector information',
        },
      ],
      ifOk: (data) => this.buildConnectorMetadata(data),
    });

    return {
      groupLabel,
      properties: fields,
    };
  }

  private buildConnectorMetadata(data: DashboardPage) {
    const fields = [
      {
        icon: 'link',
        label: 'Connector Endpoint',
        ...this.propertyGridUtils.guessValue(data.connectorEndpoint),
      },
      {
        icon: 'category',
        label: this.participantIdLocalization.participantId,
        ...this.propertyGridUtils.guessValue(data.connectorParticipantId),
      },
      {
        icon: 'title',
        label: 'Title',
        ...this.propertyGridUtils.guessValue(data.connectorTitle),
      },
      {
        icon: 'apartment',
        label: 'Curator Organization Name',
        ...this.propertyGridUtils.guessValue(data.connectorCuratorName),
      },
      {
        icon: 'apartment',
        label: 'Curator URL',
        ...this.propertyGridUtils.guessValue(data.connectorCuratorUrl),
      },
      {
        icon: 'title',
        label: 'Description',
        ...this.propertyGridUtils.guessValue(data.connectorDescription),
      },
      {
        icon: 'contact_support',
        label: 'Maintainer Organization Name',
        ...this.propertyGridUtils.guessValue(data.connectorMaintainerName),
      },
      {
        icon: 'contact_support',
        label: 'Maintainer URL',
        ...this.propertyGridUtils.guessValue(data.connectorMaintainerUrl),
      },
    ];

    if (data.connectorDapsConfig != null) {
      fields.push(
        {
          icon: 'vpn_key',
          label: 'DAPS Token URL',
          ...this.propertyGridUtils.guessValue(
            data.connectorDapsConfig.tokenUrl,
          ),
        },
        {
          icon: 'lock',
          label: 'DAPS JWKS URL',
          ...this.propertyGridUtils.guessValue(
            data.connectorDapsConfig.jwksUrl,
          ),
        },
      );
    }

    if (data.connectorMiwConfig != null) {
      fields.push(
        {
          icon: 'category',
          label: 'MIW Authority ID',
          ...this.propertyGridUtils.guessValue(
            data.connectorMiwConfig.authorityId,
          ),
        },
        {
          icon: 'link',
          label: 'MIW URL',
          ...this.propertyGridUtils.guessValue(data.connectorMiwConfig.url),
        },
        {
          icon: 'vpn_key',
          label: 'MIW Token URL',
          ...this.propertyGridUtils.guessValue(
            data.connectorMiwConfig.tokenUrl,
          ),
        },
      );
    }

    return fields;
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
    dashboardPageData: Fetched<DashboardPage>,
  ): PropertyGridGroup[] {
    const fieldGroups: PropertyGridGroup[] = [
      this.buildConnectorPropertyGridGroup(null, dashboardPageData),
      this.buildConnectorVersionGroup(
        lastCommitInformation,
        UiBuildDate,
        UiCommitDetails,
      ),
    ];

    return fieldGroups.filter((it) => it.properties.length);
  }
}
