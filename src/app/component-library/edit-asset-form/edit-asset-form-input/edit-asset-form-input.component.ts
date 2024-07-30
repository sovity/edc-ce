import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ValidationMessages} from 'src/app/core/validators/validation-messages';

@Component({
  selector: 'edit-asset-form-input',
  templateUrl: './edit-asset-form-input.component.html',
})
export class EditAssetFormInputComponent {
  @Input() ctrl!: FormControl<string>;
  @Input() fieldId = 'missing-id-' + Math.random().toString(36).substring(7);
  @Input() label!: string;
  @Input() placeholder: string = '...';
  @Input() hideHint: boolean = false;

  constructor(public validationMessages: ValidationMessages) {}
}
