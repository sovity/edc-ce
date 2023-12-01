import {COMMA, ENTER, SEMICOLON} from '@angular/cdk/keycodes';
import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatChipInputEvent} from '@angular/material/chips';
import {ParticipantIdLocalization} from '../../../../core/services/participant-id-localization';
import {removeOnce} from '../../../../core/utils/array-utils';

@Component({
  selector: 'participant-id-select',
  templateUrl: 'participant-id-select.component.html',
})
export class ParticipantIdSelectComponent {
  separatorKeysCodes: number[] = [ENTER, COMMA, SEMICOLON];

  @Input()
  control!: FormControl<string[]>;

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  constructor(public participantIdLocalization: ParticipantIdLocalization) {}

  remove(participantId: string) {
    this.control.setValue(removeOnce(this.control.value, participantId));
  }

  add(event: MatChipInputEvent): void {
    const participantIds = (event.value || '')
      .split(/[,;]/)
      .map((it) => it.trim())
      .filter((it) => it);
    if (participantIds.length) {
      this.control.setValue([...this.control.value, ...participantIds]);
    }
    event.chipInput.clear();
  }
}
