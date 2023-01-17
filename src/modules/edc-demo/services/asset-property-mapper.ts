import {Injectable} from '@angular/core';
import {LanguageSelectItemService} from "../components/language-select/language-select-item.service";
import {TransportModeSelectItemService} from "../components/transport-mode-select/transport-mode-select-item.service";
import {DataCategorySelectItemService} from "../components/data-category-select/data-category-select-item.service";
import {
  DataSubcategorySelectItemService
} from "../components/data-subcategory-select/data-subcategory-select-item.service";
import {Asset} from "../models/asset";
import {AssetProperties} from "./asset-properties";
import {AssetEditorDialogFormValue} from "../components/asset-editor-dialog/asset-editor-dialog-form-model";
import {trimmedOrNull} from "../utils/string-utils";
import {AppConfigService} from "../../app/config/app-config.service";
import {ActiveFeatureSet} from "../../app/config/active-feature-set";


/**
 * Maps between EDC Asset and our type safe asset
 */
@Injectable({
  providedIn: 'root'
})
export class AssetPropertyMapper {
  constructor(
    private languageSelectItemService: LanguageSelectItemService,
    private transportModeSelectItemService: TransportModeSelectItemService,
    private dataCategorySelectItemService: DataCategorySelectItemService,
    private dataSubcategorySelectItemService: DataSubcategorySelectItemService,
    private appConfigService: AppConfigService,
    private activeFeatureSet: ActiveFeatureSet,
  ) {
  }

  readProperties(props: Record<string, string | null>): Asset {
    const language = props[AssetProperties.language]
      ? this.languageSelectItemService.findById(props[AssetProperties.language]!)
      : null;
    const dataCategory = props[AssetProperties.dataCategory]
      ? this.dataCategorySelectItemService.findById(props[AssetProperties.dataCategory]!)
      : null;
    const dataSubcategory = props[AssetProperties.dataSubcategory]
      ? this.dataSubcategorySelectItemService.findById(props[AssetProperties.dataSubcategory]!)
      : null;
    const transportMode = props[AssetProperties.transportMode]
      ? this.transportModeSelectItemService.findById(props[AssetProperties.transportMode]!)
      : null;
    const keywords = (props[AssetProperties.keywords] ?? '').split(",").map(it => it.trim()).filter(it => it)

    const id = props[AssetProperties.id] ?? 'no-id-was-set';

    return {
      id,
      name: props[AssetProperties.name] ?? id,
      version: props[AssetProperties.version],
      contentType: props[AssetProperties.contentType],
      originator: props[AssetProperties.originator],
      originatorOrganization: props[AssetProperties.originatorOrganization],
      keywords,
      description: props[AssetProperties.description],
      language,
      publisher: props[AssetProperties.publisher],
      standardLicense: props[AssetProperties.standardLicense],
      endpointDocumentation: props[AssetProperties.endpointDocumentation],

      dataCategory,
      dataSubcategory,
      dataModel: props[AssetProperties.dataModel],
      geoReferenceMethod: props[AssetProperties.geoReferenceMethod],
      transportMode,
    }
  }

  buildProperties(formValue: AssetEditorDialogFormValue): Record<string, string | null> {
    const {metadata, advanced, datasource} = formValue;
    const props: Record<string, string | null> = {};
    props[AssetProperties.id] = trimmedOrNull(metadata?.id)
    props[AssetProperties.name] = trimmedOrNull(metadata?.name)
    props[AssetProperties.version] = trimmedOrNull(metadata?.version)
    props[AssetProperties.originator] = trimmedOrNull(this.appConfigService.config.originator)
    props[AssetProperties.originatorOrganization] = trimmedOrNull(this.appConfigService.config.originatorOrganization)
    props[AssetProperties.keywords] = trimmedOrNull(metadata?.keywords?.join(", "))
    props[AssetProperties.contentType] = trimmedOrNull(metadata?.contenttype)
    props[AssetProperties.description] = trimmedOrNull(metadata?.description)
    props[AssetProperties.language] = metadata?.language?.id ?? null

    props[AssetProperties.publisher] = trimmedOrNull(datasource?.publisher)
    props[AssetProperties.standardLicense] = trimmedOrNull(datasource?.standardLicense)
    props[AssetProperties.endpointDocumentation] = trimmedOrNull(datasource?.endpointDocumentation)

    if (this.activeFeatureSet.isMds()) {
      props[AssetProperties.dataCategory] = advanced?.dataCategory?.id ?? null
      props[AssetProperties.dataSubcategory] = advanced?.dataSubcategory?.id ?? null
      props[AssetProperties.dataModel] = trimmedOrNull(advanced?.dataModel)
      props[AssetProperties.geoReferenceMethod] = trimmedOrNull(advanced?.geoReferenceMethod)
      props[AssetProperties.transportMode] = advanced?.transportMode?.id ?? null
    }
    return this.removeNullValues(props);
  }

  private removeNullValues(obj: Record<string, string | null>): Record<string, string> {
    return Object.fromEntries(Object.entries(obj).filter(([_, v]) => v != null) as [string, string][])
  }
}
