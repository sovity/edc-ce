import {Injectable} from '@angular/core';
import {UiAsset} from '@sovity.de/edc-client';
import {DataCategorySelectItem} from 'src/app/component-library/edit-asset-form/data-category-select/data-category-select-item';
import {DataCategorySelectItemService} from 'src/app/component-library/edit-asset-form/data-category-select/data-category-select-item.service';
import {DataSubcategorySelectItem} from 'src/app/component-library/edit-asset-form/data-subcategory-select/data-subcategory-select-item';
import {DataSubcategorySelectItemService} from 'src/app/component-library/edit-asset-form/data-subcategory-select/data-subcategory-select-item.service';
import {LanguageSelectItem} from 'src/app/component-library/edit-asset-form/language-select/language-select-item';
import {LanguageSelectItemService} from 'src/app/component-library/edit-asset-form/language-select/language-select-item.service';
import {TransportModeSelectItem} from 'src/app/component-library/edit-asset-form/transport-mode-select/transport-mode-select-item';
import {TransportModeSelectItemService} from 'src/app/component-library/edit-asset-form/transport-mode-select/transport-mode-select-item.service';
import {AdditionalAssetProperty, UiAssetMapped} from './models/ui-asset-mapped';

/**
 * Maps between EDC Asset and our type safe asset
 */
@Injectable({
  providedIn: 'root',
})
export class AssetBuilder {
  constructor(
    private languageSelectItemService: LanguageSelectItemService,
    private transportModeSelectItemService: TransportModeSelectItemService,
    private dataCategorySelectItemService: DataCategorySelectItemService,
    private dataSubcategorySelectItemService: DataSubcategorySelectItemService,
  ) {}

  buildAsset(asset: UiAsset): UiAssetMapped {
    const {
      language,
      dataCategory,
      dataSubcategory,
      transportMode,
      ...assetProperties
    } = asset;

    const {
      customJsonAsString,
      customJsonLdAsString,
      privateCustomJsonAsString,
      privateCustomJsonLdAsString,
    } = asset;

    return {
      ...assetProperties,
      language: this.getLanguageItem(language),
      dataCategory: this.getDataCategoryItem(dataCategory),
      dataSubcategory: this.getDataSubcategoryItem(dataSubcategory),
      transportMode: this.getTransportModeItem(transportMode),
      customJsonProperties: this.buildAdditionalProperties(customJsonAsString),
      customJsonLdProperties:
        this.buildAdditionalProperties(customJsonLdAsString),
      privateCustomJsonProperties: this.buildAdditionalProperties(
        privateCustomJsonAsString,
      ),
      privateCustomJsonLdProperties: this.buildAdditionalProperties(
        privateCustomJsonLdAsString,
      ),
    };
  }

  private getTransportModeItem(
    transportMode: string | undefined,
  ): TransportModeSelectItem | null {
    return transportMode == null
      ? null
      : this.transportModeSelectItemService.findById(transportMode);
  }

  private getDataSubcategoryItem(
    dataSubcategory: string | undefined,
  ): DataSubcategorySelectItem | null {
    return dataSubcategory == null
      ? null
      : this.dataSubcategorySelectItemService.findById(dataSubcategory);
  }

  private getDataCategoryItem(
    dataCategory: string | undefined,
  ): DataCategorySelectItem | null {
    return dataCategory == null
      ? null
      : this.dataCategorySelectItemService.findById(dataCategory);
  }

  private getLanguageItem(
    language: string | undefined,
  ): LanguageSelectItem | null {
    return language == null
      ? null
      : this.languageSelectItemService.findById(language);
  }

  private buildAdditionalProperties(
    json: string | undefined,
  ): AdditionalAssetProperty[] {
    const obj = this.tryParseJsonObj(json || '{}');
    return Object.entries(obj).map(
      ([key, value]): AdditionalAssetProperty => ({
        key: `${key}`,
        value:
          typeof value === 'object'
            ? JSON.stringify(value, null, 2)
            : `${value}`,
      }),
    );
  }

  private tryParseJsonObj(json: string): any {
    const bad = {'Conversion Failure': `Bad JSON: ${json}`};

    try {
      const parsed = JSON.parse(json);
      if (parsed == null) {
        return {};
      } else if (typeof parsed === 'object') {
        return parsed;
      }
    } catch (e) {}

    return bad;
  }
}
