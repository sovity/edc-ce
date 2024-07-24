import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {combineLatest, distinctUntilChanged, pairwise} from 'rxjs';
import {map} from 'rxjs/operators';
import {ActiveFeatureSet} from 'src/app/core/config/active-feature-set';
import {value$} from 'src/app/core/utils/form-group-utils';
import {noWhitespacesOrColonsValidator} from 'src/app/core/validators/no-whitespaces-or-colons-validator';
import {EditAssetFormValidators} from './edit-asset-form-validators';
import {AssetEditDialogMode} from './model/asset-edit-dialog-mode';
import {
  AssetGeneralFormModel,
  AssetGeneralFormValue,
} from './model/asset-general-form-model';

@Injectable()
export class AssetGeneralFormBuilder {
  constructor(
    private formBuilder: FormBuilder,
    private activeFeatureSet: ActiveFeatureSet,
    private editAssetFormValidators: EditAssetFormValidators,
  ) {}

  buildFormGroup(
    initial: AssetGeneralFormValue,
    mode: AssetEditDialogMode,
  ): FormGroup<AssetGeneralFormModel> {
    const general: FormGroup<AssetGeneralFormModel> =
      this.formBuilder.nonNullable.group({
        id: [
          initial.id!,
          [Validators.required, noWhitespacesOrColonsValidator],
          this.editAssetFormValidators.isValidId(),
        ],
        name: [initial.name!, Validators.required],
        description: [initial.description!],
        keywords: [initial.keywords!],
        showAdvancedFields: [initial.showAdvancedFields || false],
        version: [initial.version!],
        contentType: [initial.contentType!],
        language: [initial.language || null],
        publisher: [initial.publisher!],
        standardLicense: [initial.standardLicense!],
        endpointDocumentation: [initial.endpointDocumentation!],
      });

    if (this.activeFeatureSet.hasMdsFields()) {
      general.addControl(
        'dataCategory',
        this.formBuilder.control(
          initial.dataCategory || null,
          Validators.required,
        ),
      );
      general.addControl(
        'dataSubcategory',
        this.formBuilder.control(initial.dataSubcategory || null),
      );
    }

    if (mode === 'CREATE') {
      this.initIdGeneration(general.controls.id, general.controls.name);
    } else {
      general.controls.id.disable();
    }

    return general;
  }

  private initIdGeneration(
    idCtrl: FormControl<string>,
    nameCtrl: FormControl<string>,
  ) {
    combineLatest([value$<string>(nameCtrl).pipe(distinctUntilChanged())])
      .pipe(
        map(([title]) => this.generateId(title)),
        pairwise(),
      )
      .subscribe(([previousId, currentId]) => {
        if (!idCtrl.value || idCtrl.value === previousId) {
          idCtrl.setValue(currentId);
          idCtrl.markAsTouched();
          idCtrl.updateValueAndValidity();
        }
      });
  }

  private generateId(name: string | null) {
    if (!name) {
      return '';
    }
    return this.cleanIdComponent(name);
  }

  private cleanIdComponent(s: string | null) {
    return (s ?? '')
      .trim()
      .replace(':', '-')
      .replaceAll(' ', '-')
      .toLowerCase();
  }
}
