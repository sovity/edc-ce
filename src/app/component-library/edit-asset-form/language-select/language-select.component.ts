import {Component, Input} from '@angular/core';
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

  constructor(public items: LanguageSelectItemService) {}
}
