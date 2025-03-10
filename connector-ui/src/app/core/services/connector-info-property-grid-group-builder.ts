/*
 * Copyright 2025 sovity GmbH
 * Copyright 2024 Fraunhofer Institute for Applied Information Technology FIT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer FIT - contributed initial internationalization support
 */
import {Inject, Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {BuildInfo, DashboardPage} from '@sovity.de/edc-client';
import {formatDateAgo} from '../../shared/common/ago/formatDateAgo';
import {JsonDialogComponent} from '../../shared/common/json-dialog/json-dialog.component';
import {JsonDialogData} from '../../shared/common/json-dialog/json-dialog.data';
import {PropertyGridGroup} from '../../shared/common/property-grid-group/property-grid-group';
import {PropertyGridField} from '../../shared/common/property-grid/property-grid-field';
import {PropertyGridFieldService} from '../../shared/common/property-grid/property-grid-field.service';
import {APP_CONFIG, AppConfig} from '../config/app-config';
import {Fetched} from './models/fetched';
import {ParticipantIdLocalization} from './participant-id-localization';

@Injectable({providedIn: 'root'})
export class ConnectorInfoPropertyGridGroupBuilder {
  constructor(
    private participantIdLocalization: ParticipantIdLocalization,
    private propertyGridUtils: PropertyGridFieldService,
    private matDialog: MatDialog,
    private translateService: TranslateService,
    @Inject(APP_CONFIG) private config: AppConfig,
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

    if (data.connectorCxDidConfig != null) {
      fields.push(
        {
          icon: 'category',
          label: 'Your DID',
          ...this.propertyGridUtils.guessValue(data.connectorCxDidConfig.myDid),
        },
        {
          icon: 'vpn_key',
          label: 'Wallet Token URL',
          ...this.propertyGridUtils.guessValue(
            data.connectorCxDidConfig.walletTokenUrl,
          ),
        },
        {
          icon: 'category',
          label: 'Trusted VC Issuer',
          ...this.propertyGridUtils.guessValue(
            data.connectorCxDidConfig.trustedVcIssuer,
          ),
        },
        {
          icon: 'link',
          label: 'BDRS URL',
          ...this.propertyGridUtils.guessValue(
            data.connectorCxDidConfig.bdrsUrl,
          ),
        },
        {
          icon: 'link',
          label: 'DIM URL',
          ...this.propertyGridUtils.guessValue(
            data.connectorCxDidConfig.dimUrl,
          ),
        },
      );
    }

    return fields;
  }

  buildConnectorVersionGroup(buildInfo: Fetched<BuildInfo>): PropertyGridGroup {
    const properties: PropertyGridField[] = [
      {
        icon: 'link',
        label: 'Connector Version',
        text: buildInfo.dataOrUndefined?.buildVersion,
        tooltip: formatDateAgo(buildInfo.dataOrUndefined?.buildDate),
        copyButton: true,
      },
    ];

    if (this.config.buildVersion !== buildInfo.dataOrUndefined?.buildVersion) {
      properties.push({
        icon: 'link',
        label: 'UI Version',
        text: this.config.buildVersion,
        tooltip: formatDateAgo(this.config.buildDate),
        copyButton: true,
      });
    }

    return {
      groupLabel: 'Version Information',
      properties,
    };
  }

  buildPropertyGridGroups(
    versionInfo: Fetched<BuildInfo>,
    dashboardPageData: Fetched<DashboardPage>,
  ): PropertyGridGroup[] {
    const fieldGroups: PropertyGridGroup[] = [
      this.buildConnectorPropertyGridGroup(null, dashboardPageData),
      this.buildConnectorVersionGroup(versionInfo),
    ];

    return fieldGroups.filter((it) => it.properties.length);
  }
}
