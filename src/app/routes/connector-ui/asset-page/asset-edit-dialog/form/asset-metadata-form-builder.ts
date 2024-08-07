import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {combineLatest, distinctUntilChanged, pairwise} from 'rxjs';
import {map} from 'rxjs/operators';
import {value$} from '../../../../../core/utils/form-group-utils';
import {noWhitespacesOrColonsValidator} from '../../../../../core/validators/no-whitespaces-or-colons-validator';
import {urlValidator} from '../../../../../core/validators/url-validator';
import {AssetEditDialogMode} from '../asset-edit-dialog-mode';
import {AssetsIdValidatorBuilder} from '../assets-id-validator-builder';
import {
  AssetMetadataFormModel,
  AssetMetadataFormValue,
} from './model/asset-metadata-form-model';

@Injectable()
export class AssetMetadataFormBuilder {
  constructor(
    private formBuilder: FormBuilder,
    private assetsIdValidatorBuilder: AssetsIdValidatorBuilder,
  ) {}

  buildFormGroup(
    initial: AssetMetadataFormValue,
    mode: AssetEditDialogMode,
  ): FormGroup<AssetMetadataFormModel> {
    const metadata: FormGroup<AssetMetadataFormModel> =
      this.formBuilder.nonNullable.group({
        id: [
          initial?.id!,
          [Validators.required, noWhitespacesOrColonsValidator],
          [this.assetsIdValidatorBuilder.assetIdDoesNotExistsValidator()],
        ],
        title: [initial?.title!, Validators.required],
        version: [initial?.version!],
        contentType: [initial?.contentType!],
        description: [initial?.description!],
        keywords: [initial?.keywords!],
        language: [initial?.language ?? null],
        publisher: [initial?.publisher!, urlValidator],
        standardLicense: [initial?.standardLicense!, urlValidator],
        endpointDocumentation: [initial?.endpointDocumentation!, urlValidator],
      });

    // generate id from name and version(if available)
    if (mode === 'CREATE') {
      this.initIdGeneration(
        metadata.controls.id,
        metadata.controls.title,
        metadata.controls.version,
      );
    } else {
      metadata.controls.id.disable();
    }

    return metadata;
  }

  private initIdGeneration(
    idCtrl: FormControl<string>,
    titleCtrl: FormControl<string>,
    versionCtrl: FormControl<string>,
  ) {
    combineLatest([
      value$<string>(titleCtrl).pipe(distinctUntilChanged()),
      value$<string>(versionCtrl).pipe(distinctUntilChanged()),
    ])
      .pipe(
        map(([title, version]) => this.generateId(title, version)),
        pairwise(),
      )
      .subscribe(([previousId, currentId]) => {
        if (!idCtrl.value || idCtrl.value === previousId) {
          idCtrl.setValue(currentId);
        }
      });
  }

  private generateId(title: string | null, version: string | null) {
    if (!title) {
      return '';
    }
    const titleClean = this.cleanIdComponent(title);
    const versionClean = this.cleanIdComponent(version);
    return version ? `${titleClean}-${versionClean}` : titleClean;
  }

  private cleanIdComponent(s: string | null) {
    return (s ?? '')
      .trim()
      .replace(':', '-')
      .replaceAll(' ', '-')
      .toLowerCase();
  }
}
