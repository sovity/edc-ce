import {COMMA, ENTER, SEMICOLON} from '@angular/cdk/keycodes';
import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatChipInputEvent} from '@angular/material/chips';
import {removeOnce} from '../../../../core/utils/array-utils';

@Component({
  selector: 'keyword-select',
  templateUrl: 'keyword-select.component.html',
})
export class KeywordSelectComponent {
  separatorKeysCodes: number[] = [ENTER, COMMA, SEMICOLON];

  @Input()
  label!: string;

  @Input()
  control!: FormControl<string[]>;

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  remove(keyword: string) {
    this.control.setValue(removeOnce(this.control.value, keyword));
  }

  add(event: MatChipInputEvent): void {
    const keywords = (event.value || '')
      .split(/[,;]/)
      .map((it) => it.trim())
      .filter((it) => it);
    if (keywords.length) {
      this.control.setValue([...this.control.value, ...keywords]);
    }
    event.chipInput.clear();
  }
}
