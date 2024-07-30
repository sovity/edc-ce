import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ValidationMessages} from 'src/app/core/validators/validation-messages';

@Component({
  selector: 'edit-asset-form-textarea',
  templateUrl: './edit-asset-form-textarea.component.html',
})
export class EditAssetFormTextareaComponent {
  @Input() ctrl!: FormControl<string>;
  @Input() fieldId = 'missing-id-' + Math.random().toString(36).substring(7);
  @Input() label!: string;
  @Input() placeholder: string = '...';
  @Input() hideHint: boolean = false;
  @Input() textareaClasses: string = 'h-36';

  constructor(public validationMessages: ValidationMessages) {}
}
