import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {DashboardPage} from '@sovity.de/edc-client';
import {JsonDialogComponent} from '../../shared/common/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../shared/common/json-dialog/json-dialog.data';
import {PropertyGridGroup} from '../../shared/common/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../shared/common/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../shared/common/property-grid/property-grid-field.service';
import {LastCommitInfo} from './api/model/last-commit-info';
import {Fetched} from './models/fetched';
import {ParticipantIdLocalization} from './participant-id-localization';

@Injectable({providedIn: 'root'})
export class ConnectorInfoPropertyGridGroupBuilder {
  constructor(
    private participantIdLocalization: ParticipantIdLocalization,
    private propertyGridUtils: PropertyGridFieldService,
    private matDialog: MatDialog,
    private translateService: TranslateService,
  ) {}

  private onShowConnectorVersionClick(title: string, versionData: any) {
    const data: JsonDialogData = {
      title,
      subtitle: this.translateService.instant('general.details'),
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
    const buildProp = (
      label: string,
      dateProp: keyof LastCommitInfo,
      detailsProp: keyof LastCommitInfo,
    ) =>
      lastCommitInfo.match({
        ifOk: (lastCommitInfo) => ({
          icon: 'link',
          label,
          text: lastCommitInfo[dateProp]
            ? this.asDate(lastCommitInfo[dateProp] ?? undefined)
            : 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              [`${label} Last Commit Information"`]:
                lastCommitInfo[detailsProp],
            }),
        }),
        ifError: (error) => ({
          icon: 'link',
          label,
          text: 'Show Details',
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              Error: error.failureMessage,
            }),
        }),
        ifLoading: () => ({
          icon: 'link',
          label,
          text: 'Loading...',
        }),
      });

    return [
      buildProp('CE Extensions', 'jarBuildDate', 'jarLastCommitInfo'),
      buildProp('Connector', 'envBuildDate', 'envLastCommitInfo'),
    ];
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
            : this.translateService.instant('general.details'),
          onclick: () =>
            this.onShowConnectorVersionClick('Version Information', {
              'UI Last Commit Information': uiCommitDetails.match({
                ifOk: (uiCommitdata) => uiCommitdata,
                ifError: (error) => error.failureMessage,
                ifLoading: () =>
                  this.translateService.instant('general.still_loading'),
              }),
            }),
        },
      ],
      ifError: (error) => [
        {
          icon: 'link',
          label: 'UI Version',
          text: this.translateService.instant('general.details'),
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
          text: this.translateService.instant('general.loading'),
        },
      ],
    });
  }

  buildConnectorPropertyGridGroup(
    groupLabel: string | null,
    dashboardData: Fetched<DashboardPage>,
  ): PropertyGridGroup {
    const fields: PropertyGridField[] = dashboardData.match<
      PropertyGridField[]
    >({
      ifLoading: () => [
        {
          icon: 'info',
          label: this.translateService.instant('general.loading1'),
          text: this.translateService.instant('general.loading'),
        },
      ],
      ifError: () => [
        {
          icon: 'error',
          label: this.translateService.instant('general.error'),
          text: this.translateService.instant('services.failed_loading'),
        },
      ],
      ifOk: (data) => this.buildConnectorMetadata(data),
    });

    return {
      groupLabel,
      properties: fields,
    };
  }

  private buildConnectorMetadata(data: DashboardPage): PropertyGridField[] {
    const fields = [
      {
        icon: 'link',
        label: this.translateService.instant('general.endpoint'),
        ...this.propertyGridUtils.guessValue(data.connectorEndpoint),
      },
      {
        icon: 'category',
        label: this.participantIdLocalization.participantId,
        ...this.propertyGridUtils.guessValue(data.connectorParticipantId),
      },
      {
        icon: 'title',
        label: this.translateService.instant('general.title'),
        ...this.propertyGridUtils.guessValue(data.connectorTitle),
      },
      {
        icon: 'apartment',
        label: this.translateService.instant('services.curator_org'),
        ...this.propertyGridUtils.guessValue(data.connectorCuratorName),
      },
      {
        icon: 'apartment',
        label: this.translateService.instant('services.curator_url'),
        ...this.propertyGridUtils.guessValue(data.connectorCuratorUrl),
      },
      {
        icon: 'title',
        label: this.translateService.instant('general.description'),
        ...this.propertyGridUtils.guessValue(data.connectorDescription),
      },
      {
        icon: 'contact_support',
        label: this.translateService.instant('services.maintainer'),
        ...this.propertyGridUtils.guessValue(data.connectorMaintainerName),
      },
      {
        icon: 'contact_support',
        label: this.translateService.instant('services.main_url'),
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
