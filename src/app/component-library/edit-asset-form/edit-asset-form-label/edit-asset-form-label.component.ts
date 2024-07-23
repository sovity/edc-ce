import {Component, Input} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';

@Component({
  selector: 'edit-asset-form-label',
  templateUrl: './edit-asset-form-label.component.html',
})
export class EditAssetFormLabelComponent {
  @Input() label!: string;
  @Input() htmlFor?: string;
  @Input() ctrl?: FormControl<any>;

  isRequired(): boolean {
    return this.ctrl?.hasValidator(Validators.required) || false;
  }
}
