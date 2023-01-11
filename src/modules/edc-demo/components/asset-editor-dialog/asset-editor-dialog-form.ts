import {Injectable} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ValidatorFn, Validators} from "@angular/forms";
import {DataAddressType} from "./data-address-type";
import {
  AssetEditorDialogAdvancedFormModel,
  AssetEditorDialogDatasourceFormModel,
  AssetEditorDialogFormModel,
  AssetEditorDialogFormValue,
  AssetEditorDialogMetadataFormModel
} from "./asset-editor-dialog-form-model";
import {LanguageSelectItemService} from "../language-select/language-select-item.service";
import {isFeatureSetActive} from "../../pipes/is-active-feature-set.pipe";
import {jsonValidator} from "../../validators/json-validator";
import {urlValidator} from "../../validators/url-validator";
import {DataSubcategorySelectItem} from "../data-subcategory-select/data-subcategory-select-item";
import {TransportModeSelectItem} from "../transport-mode-select/transport-mode-select-item";
import {DataCategorySelectItem} from "../data-category-select/data-category-select-item";
import {LanguageSelectItem} from "../language-select/language-select-item";
import {noWhitespaceValidator} from "../../validators/no-whitespace-validator";
import {concat, distinctUntilChanged, of, pairwise} from "rxjs";
import {requiresPrefixValidator} from "../../validators/requires-prefix-validator";

/**
 * Handles AngularForms for AssetEditorDialog
 */
@Injectable()
export class AssetEditorDialogForm {

  all = this.buildFormGroup()

  /**
   * FormGroup for stepper step "Metadata"
   */
  metadata = this.all.controls.metadata

  /**
   * FormGroup for stepper step "Advanced"
   */
  advanced = this.all.controls.advanced

  /**
   * FormGroup for stepper step "Data Source"
   */
  datasource = this.all.controls.datasource

  /**
   * Quick access to selected data address type
   */
  get dataAddressType(): DataAddressType | null {
    return this.datasource.controls.dataAddressType.value
  }

  /**
   * Quick access to selected data category
   */
  get dataCategory(): DataCategorySelectItem | null {
    return this.advanced.controls.dataCategory.value
  }

  /**
   * Quick access to full value
   */
  get value(): AssetEditorDialogFormValue {
    return this.all.value;
  }

  constructor(
    private formBuilder: FormBuilder,
    private languageSelectItemService: LanguageSelectItemService,
  ) {
  }

  buildFormGroup(): FormGroup<AssetEditorDialogFormModel> {
    const validateIffMds = <T>(x: [T, ValidatorFn | ValidatorFn[]]): [T, ValidatorFn | ValidatorFn[]] =>
      isFeatureSetActive('mds') ? x : [x[0], []]

    const metadata: FormGroup<AssetEditorDialogMetadataFormModel> = this.formBuilder.nonNullable.group({
      id: ['', [Validators.required, noWhitespaceValidator, requiresPrefixValidator("urn:artifact:")]],
      name: ['', Validators.required],
      version: '',
      contenttype: '',
      description: '',
      keywords: [new Array<string>()],
      language: this.languageSelectItemService.english() as LanguageSelectItem | null,
    });

    // generate id from names
    this.initIdGeneration(metadata.controls.id, metadata.controls.name)

    const advanced: FormGroup<AssetEditorDialogAdvancedFormModel> = this.formBuilder.nonNullable.group({
      dataModel: '',
      dataCategory: validateIffMds([null as DataCategorySelectItem | null, Validators.required]),
      dataSubcategory: null as DataSubcategorySelectItem | null,
      transportMode: null as TransportModeSelectItem | null,
      geoReferenceMethod: '',
    });

    const datasource: FormGroup<AssetEditorDialogDatasourceFormModel> = this.formBuilder.nonNullable.group({
      dataAddressType: 'Json' as DataAddressType,
      dataDestination: '',
      baseUrl: ['', urlValidator],
      publisher: ['', urlValidator],
      standardLicense: ['', urlValidator],
      endpointDocumentation: ['', urlValidator],
    });

    // Switch Validators depending on selected datasource type
    this.activateValidationByDataAddressType(datasource, ["dataDestination", "baseUrl"], {
      'Json': {
        dataDestination: [Validators.required, jsonValidator],
      },
      'Rest-Api': {
        baseUrl: [Validators.required, urlValidator]
      }
    })

    return this.formBuilder.nonNullable.group({
      metadata,
      advanced,
      datasource
    });
  }

  /**
   * Apply validators depending on selected {@link DataAddressType}
   * @param datasource form group
   * @param keys all dataAddressType-affected fields
   * @param validators validators for each {@link DataAddressType}
   * @private
   */
  private activateValidationByDataAddressType(
    datasource: FormGroup<AssetEditorDialogDatasourceFormModel>,
    keys: (keyof AssetEditorDialogDatasourceFormModel)[],
    validators: Record<
      DataAddressType,
      Partial<Record<keyof AssetEditorDialogDatasourceFormModel, ValidatorFn | ValidatorFn[]>>
    >
  ) {
    const updateDatasourceValidators = () => {
      // Remove all validators
      keys.map(key => datasource.controls[key]).forEach(control => {
        control.clearValidators()
        control.updateValueAndValidity()
      })

      // Add validators where configured
      Object.entries(validators[datasource.controls.dataAddressType.value])
        .forEach(([control, validators]) => {
          datasource.controls[control as keyof AssetEditorDialogDatasourceFormModel].setValidators(validators)
        })
    }

    // Update now
    updateDatasourceValidators()

    // Update on dataAddressType changes
    datasource.controls.dataAddressType.valueChanges.subscribe(() => updateDatasourceValidators())
  }

  private initIdGeneration(id: FormControl<string>, name: FormControl<string>) {
    concat(of(name.value), name.valueChanges)
      .pipe(distinctUntilChanged(), pairwise())
      .subscribe(([previousName, currentName]) => {
        if (!id.value || id.value == this.generateId(previousName)) {
          // Generate ID, but leave field untouched if it was edited
          id.setValue(this.generateId(currentName))
        }
      })
  }

  private generateId(name: string) {
    const normalizedName = name.replace(":", "")
      .replaceAll(" ", "-").toLowerCase();
    return `urn:artifact:${normalizedName}`;
  }
}
