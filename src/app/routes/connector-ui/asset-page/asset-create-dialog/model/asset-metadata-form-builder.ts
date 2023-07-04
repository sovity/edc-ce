import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {combineLatest, distinctUntilChanged, pairwise} from 'rxjs';
import {map} from 'rxjs/operators';
import {value$} from '../../../../../core/utils/form-group-utils';
import {noWhitespaceValidator} from '../../../../../core/validators/no-whitespace-validator';
import {requiresPrefixValidator} from '../../../../../core/validators/requires-prefix-validator';
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
          [
            Validators.required,
            noWhitespaceValidator,
            requiresPrefixValidator('urn:artifact:'),
          ],
          [this.assetsIdValidatorBuilder.assetIdDoesNotExistsValidator()],
        ],
        name: ['', Validators.required],
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
      metadata.controls.name,
      metadata.controls.version,
    );

    return metadata;
  }

  private initIdGeneration(
    idCtrl: FormControl<string>,
    nameCtrl: FormControl<string>,
    versionCtrl: FormControl<string>,
  ) {
    combineLatest([
      value$<string>(nameCtrl).pipe(distinctUntilChanged()),
      value$<string>(versionCtrl).pipe(distinctUntilChanged()),
    ])
      .pipe(
        map(([name, version]) => this.generateId(name, version)),
        pairwise(),
      )
      .subscribe(([previousName, currentName]) => {
        if (!idCtrl.value || idCtrl.value == previousName) {
          idCtrl.setValue(currentName);
        }
      });
  }

  private generateId(name: string | null, version: string | null) {
    if (!name) return 'urn:artifact:';
    const normalizedName = name
      .replace(':', '')
      .replaceAll(' ', '-')
      .toLowerCase();
    if (version) return `urn:artifact:${normalizedName}:${version}`;
    return `urn:artifact:${normalizedName}`;
  }
}
