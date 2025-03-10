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
import {TranslateService} from '@ngx-translate/core';
import {APP_CONFIG, AppConfig} from '../config/app-config';

@Injectable({providedIn: 'root'})
export class ParticipantIdLocalization {
  constructor(
    @Inject(APP_CONFIG) private config: AppConfig,
    private translateService: TranslateService,
  ) {
    this.translateService
      .stream([
        'component_library.connector_id',
        'component_library.connector_id_plural',
        'component_library.participant_id',
        'component_library.participant_id_plural',
      ])
      .subscribe((translations) => {
        this.participantId = this.mds
          ? translations['component_library.connector_id']
          : translations['component_library.participant_id'];
        this.participantIdPlural = this.mds
          ? translations['component_library.connector_id_plural']
          : translations['component_library.participant_id_plural'];
      });
  }
  private mds = this.config.features.has('mds-connector-id');
  participantId = ''; // init, will be updated by translateService
  participantIdPlural = '';
  participantIdPlaceholder = this.mds
    ? 'MDSL1234XX.C1234XX'
    : 'other-connector-participant-id';
}
