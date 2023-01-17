import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatChipInputEvent} from '@angular/material/chips';
import {removeOnce} from '../../utils/array-utils';

@Component({
  selector: 'keyword-select',
  templateUrl: 'keyword-select.component.html',
})
export class KeywordSelectComponent {
  separatorKeysCodes: number[] = [ENTER, COMMA];

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
    const keyword = (event.value || '').trim();
    if (keyword) {
      this.control.setValue([...this.control.value, keyword]);
    }
    event.chipInput!.clear();
  }
}
