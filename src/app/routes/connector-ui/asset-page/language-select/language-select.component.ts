import {Component, HostBinding, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {LanguageSelectItem} from './language-select-item';
import {LanguageSelectItemService} from './language-select-item.service';

@Component({
  selector: 'language-select',
  templateUrl: 'language-select.component.html',
})
export class LanguageSelectComponent {
  @Input()
  label!: string;

  @Input()
  control!: FormControl<LanguageSelectItem | null>;

  @HostBinding('class.flex')
  @HostBinding('class.flex-row')
  cls = true;

  constructor(public items: LanguageSelectItemService) {}
}
