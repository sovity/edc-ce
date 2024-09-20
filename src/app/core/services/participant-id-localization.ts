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
