import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ValidationMessages} from 'src/app/core/validators/validation-messages';
import {ExpressionFormHandler} from '../../policy-editor/editor/expression-form-handler';
import {EditAssetForm} from './form/edit-asset-form';
import {DATA_SOURCE_HTTP_METHODS} from './form/http-methods';

@Component({
  selector: 'edit-asset-form',
  templateUrl: './edit-asset-form.component.html',
})
export class EditAssetFormComponent {
  @Input() isLoading!: boolean;
  @Output() submitClicked = new EventEmitter();

  methods = DATA_SOURCE_HTTP_METHODS;

  constructor(
    public form: EditAssetForm,
    public validationMessages: ValidationMessages,
    public expressionFormHandler: ExpressionFormHandler,
  ) {}
}
