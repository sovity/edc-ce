import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {concat, distinctUntilChanged, of, pairwise} from 'rxjs';
import {noWhitespaceValidator} from '../../../validators/no-whitespace-validator';
import {requiresPrefixValidator} from '../../../validators/requires-prefix-validator';
import {LanguageSelectItem} from '../../language-select/language-select-item';
import {LanguageSelectItemService} from '../../language-select/language-select-item.service';
import {AssetMetadataFormModel} from './asset-metadata-form-model';

@Injectable()
export class AssetMetadataFormBuilder {
  constructor(
    private formBuilder: FormBuilder,
    private languageSelectItemService: LanguageSelectItemService,
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
        ],
        name: ['', Validators.required],
        version: '',
        contentType: '',
        description: '',
        keywords: [new Array<string>()],
        language:
          this.languageSelectItemService.english() as LanguageSelectItem | null,
      });

    // generate id from names
    this.initIdGeneration(metadata.controls.id, metadata.controls.name);

    return metadata;
  }

  private initIdGeneration(id: FormControl<string>, name: FormControl<string>) {
    concat(of(name.value), name.valueChanges)
      .pipe(distinctUntilChanged(), pairwise())
      .subscribe(([previousName, currentName]) => {
        if (!id.value || id.value == this.generateId(previousName)) {
          // Generate ID, but leave field untouched if it was edited
          id.setValue(this.generateId(currentName));
        }
      });
  }

  private generateId(name: string) {
    const normalizedName = name
      .replace(':', '')
      .replaceAll(' ', '-')
      .toLowerCase();
    return `urn:artifact:${normalizedName}`;
  }
}
