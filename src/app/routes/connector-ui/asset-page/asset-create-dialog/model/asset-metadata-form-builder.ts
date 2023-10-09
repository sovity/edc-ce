import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {combineLatest, distinctUntilChanged, pairwise} from 'rxjs';
import {map} from 'rxjs/operators';
import {value$} from '../../../../../core/utils/form-group-utils';
import {noWhitespacesOrColonsValidator} from '../../../../../core/validators/no-whitespaces-or-colons-validator';
import {urlValidator} from '../../../../../core/validators/url-validator';
import {LanguageSelectItem} from '../../language-select/language-select-item';
import {LanguageSelectItemService} from '../../language-select/language-select-item.service';
import {AssetsIdValidatorBuilder} from '../assets-id-validator-builder';
import {AssetMetadataFormModel} from './asset-metadata-form-model';

@Injectable()
export class AssetMetadataFormBuilder {
  constructor(
    private formBuilder: FormBuilder,
    private languageSelectItemService: LanguageSelectItemService,
    private assetsIdValidatorBuilder: AssetsIdValidatorBuilder,
  ) {}

  buildFormGroup(): FormGroup<AssetMetadataFormModel> {
    const metadata: FormGroup<AssetMetadataFormModel> =
      this.formBuilder.nonNullable.group({
        id: [
          '',
          [Validators.required, noWhitespacesOrColonsValidator],
          [this.assetsIdValidatorBuilder.assetIdDoesNotExistsValidator()],
        ],
        title: ['', Validators.required],
        version: '',
        contentType: '',
        description: '',
        keywords: [new Array<string>()],
        language:
          this.languageSelectItemService.english() as LanguageSelectItem | null,
        publisher: ['', urlValidator],
        standardLicense: ['', urlValidator],
        endpointDocumentation: ['', urlValidator],
      });

    // generate id from name and version(if available)
    this.initIdGeneration(
      metadata.controls.id,
      metadata.controls.title,
      metadata.controls.version,
    );

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
