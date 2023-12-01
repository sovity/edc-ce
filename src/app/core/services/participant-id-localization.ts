import {Inject, Injectable} from '@angular/core';
import {APP_CONFIG, AppConfig} from '../config/app-config';

@Injectable({providedIn: 'root'})
export class ParticipantIdLocalization {
  private mds = this.config.features.has('mds-connector-id');
  participantId = this.mds ? 'Connector ID' : 'Participant ID';
  participantIdPlural = this.participantId + 's';
  participantIdPlaceholder = this.mds
    ? 'MDSL1234XX.C1234XX'
    : 'other-connector-participant-id';

  constructor(@Inject(APP_CONFIG) private config: AppConfig) {}
}
