import {Inject, Injectable} from '@angular/core';
import {LanguageSelectItemService} from "../components/language-select/language-select-item.service";
import {TransportModeSelectItemService} from "../components/transport-mode-select/transport-mode-select-item.service";
import {DataCategorySelectItemService} from "../components/data-category-select/data-category-select-item.service";
import {
  DataSubcategorySelectItemService
} from "../components/data-subcategory-select/data-subcategory-select-item.service";
import {Asset} from "../models/asset";
import {AssetProperties} from "./asset-properties";
import {AssetEditorDialogFormValue} from "../components/asset-editor-dialog/asset-editor-dialog-form-model";
import {isFeatureSetActive} from "../pipes/is-active-feature-set.pipe";
import {trimmedOrNull} from "../utils/string-utils";
import {CONNECTOR_ORIGINATOR, CONNECTOR_ORIGINATOR_ORGANIZATON} from "../../edc-dmgmt-client";
import urlJoin from 'url-join';


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
    @Inject(CONNECTOR_ORIGINATOR) private connectorOriginator: string,
    @Inject(CONNECTOR_ORIGINATOR_ORGANIZATON) private connectorOriginatorOrganization: string
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

    const id = props[AssetProperties.assetIdEdcc] ?? props[AssetProperties.assetIdIds] ?? 'no-known-id-field-was-set';

    return {
      id,
      idsId: props[AssetProperties.assetIdIds],
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
    props[AssetProperties.assetIdEdcc] = trimmedOrNull(metadata?.id)
    props[AssetProperties.assetIdIds] = this.buildIdsId(props[AssetProperties.assetIdEdcc]!)
    props[AssetProperties.name] = trimmedOrNull(metadata?.name)
    props[AssetProperties.version] = trimmedOrNull(metadata?.version)
    props[AssetProperties.originator] = trimmedOrNull(this.connectorOriginator)
    props[AssetProperties.originatorOrganization] = trimmedOrNull(this.connectorOriginatorOrganization)
    props[AssetProperties.keywords] = trimmedOrNull(metadata?.keywords?.join(", "))
    props[AssetProperties.contentType] = trimmedOrNull(metadata?.contenttype)
    props[AssetProperties.description] = trimmedOrNull(metadata?.description)
    props[AssetProperties.language] = metadata?.language?.id ?? null

    props[AssetProperties.publisher] = trimmedOrNull(datasource?.publisher)
    props[AssetProperties.standardLicense] = trimmedOrNull(datasource?.standardLicense)
    props[AssetProperties.endpointDocumentation] = trimmedOrNull(datasource?.endpointDocumentation)

    if (isFeatureSetActive('mds')) {
      props[AssetProperties.dataCategory] = advanced?.dataCategory?.id ?? null
      props[AssetProperties.dataSubcategory] = advanced?.dataSubcategory?.id ?? null
      props[AssetProperties.dataModel] = trimmedOrNull(advanced?.dataModel)
      props[AssetProperties.geoReferenceMethod] = trimmedOrNull(advanced?.geoReferenceMethod)
      props[AssetProperties.transportMode] = advanced?.transportMode?.id ?? null
    }
    return props;
  }

  private buildIdsId(edccId: string): string {
    return urlJoin(this.connectorOriginator ?? `https://originator-undefined-in-edc-ui`, "assets", edccId);
  }
}
