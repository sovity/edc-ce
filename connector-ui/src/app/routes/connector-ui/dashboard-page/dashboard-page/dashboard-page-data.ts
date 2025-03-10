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
import {Fetched} from '../../../../core/services/models/fetched';
import {PropertyGridGroup} from '../../../../shared/common/property-grid-group/property-grid-group';
import {DonutChartData} from '../dashboard-donut-chart/donut-chart-data';

export interface DashboardPageData {
  incomingTransfersChart: Fetched<DonutChartData>;
  outgoingTransfersChart: Fetched<DonutChartData>;
  numContractAgreements: Fetched<number>;
  numAssets: Fetched<number>;
  numCatalogEntries: Fetched<number>;
  numContractDefinitions: Fetched<number>;
  numPolicies: Fetched<number>;
  numCatalogs: Fetched<number>;
  connectorProperties: PropertyGridGroup[];
  connectorEndpointAndParticipantId: string;
  title: string;
  description: string;
}

export function defaultDashboardData(): DashboardPageData {
  return {
    incomingTransfersChart: Fetched.empty(),
    outgoingTransfersChart: Fetched.empty(),
    numContractAgreements: Fetched.empty(),
    numAssets: Fetched.empty(),
    numCatalogEntries: Fetched.empty(),
    numPolicies: Fetched.empty(),
    numContractDefinitions: Fetched.empty(),
    numCatalogs: Fetched.empty(),
    connectorProperties: [],
    connectorEndpointAndParticipantId: 'Loading...',
    title: 'Loading...',
    description: 'Loading...',
  };
}
