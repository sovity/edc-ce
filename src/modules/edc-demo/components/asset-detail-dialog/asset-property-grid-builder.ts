import {Injectable} from '@angular/core';
import {ActiveFeatureSet} from '../../../app/config/active-feature-set';
import {Asset} from '../../models/asset';
import {PropertyGridField} from '../property-grid/property-grid-field';
import {PropertyGridUtils} from '../property-grid/property-grid-utils';

@Injectable()
export class AssetPropertyGridBuilder {
  constructor(
    private activeFeatureSet: ActiveFeatureSet,
    private propertyGridUtils: PropertyGridUtils,
  ) {}

  buildPropertyGrid(asset: Asset): PropertyGridField[] {
    let fields: PropertyGridField[] = [
      {
        icon: 'category',
        label: 'ID',
        ...this.propertyGridUtils.guessValue(asset.id),
      },
      {
        icon: 'file_copy',
        label: 'Version',
        ...this.propertyGridUtils.guessValue(asset.version),
      },
      {
        icon: 'language',
        label: 'Language',
        ...this.propertyGridUtils.guessValue(asset.language?.label),
      },

      {
        icon: 'apartment',
        label: 'Publisher',
        ...this.propertyGridUtils.guessValue(asset.publisher),
      },
      {
        icon: 'bookmarks',
        label: 'Endpoint Documentation',
        ...this.propertyGridUtils.guessValue(asset.endpointDocumentation),
      },
      {
        icon: 'gavel',
        label: 'Standard License',
        ...this.propertyGridUtils.guessValue(asset.standardLicense),
      },
      {
        icon: 'category',
        label: 'Content Type',
        ...this.propertyGridUtils.guessValue(asset.contentType),
      },
    ];

    // MDS Specific Fields
    if (this.activeFeatureSet.hasMdsFields()) {
      fields.push(
        {
          icon: 'commute',
          label: 'Transport Mode',
          ...this.propertyGridUtils.guessValue(asset.transportMode?.label),
        },
        {
          icon: 'commute',
          label: 'Data Category',
          ...this.propertyGridUtils.guessValue(asset.dataCategory?.label),
        },
        {
          icon: 'commute',
          label: 'Data Subcategory',
          ...this.propertyGridUtils.guessValue(asset.dataSubcategory?.label),
        },
        {
          icon: 'category',
          label: 'Data Model',
          ...this.propertyGridUtils.guessValue(asset.dataModel),
        },
      );
    }

    fields.push(
      {
        icon: 'link',
        label: 'Connector Endpoint',
        ...this.propertyGridUtils.guessValue(asset.originator),
      },
      {
        icon: 'account_circle',
        label: 'Organization',
        ...this.propertyGridUtils.guessValue(asset.originatorOrganization),
      },
    );

    return fields;
  }
}
