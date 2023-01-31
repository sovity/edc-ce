import {Injectable} from '@angular/core';
import {ActiveFeatureSet} from '../../../app/config/active-feature-set';
import {Asset} from '../../models/asset';
import {validUrlPattern} from '../../validators/url-validator';
import {AssetDetailDialogField} from './asset-detail-dialog-field';


@Injectable()
export class AssetDetailDialogFieldBuilder {
  constructor(private activeFeatureSet: ActiveFeatureSet) {
  }

  buildFields(asset: Asset): AssetDetailDialogField[] {
    const tryValue = (s?: string | null) => s || '-';
    const tryUrl = (s?: string | null): string | undefined =>
      s?.match(validUrlPattern) ? s : undefined;

    let fields: AssetDetailDialogField[] = [
      {
        icon: 'file_copy',
        label: 'Version',
        text: tryValue(asset.version),
        additionalClasses: 'break-all',
      },
      {
        icon: 'category',
        label: 'Content Type',
        text: tryValue(asset.contentType),
        additionalClasses: 'break-all',
      },
      {
        icon: 'language',
        label: 'Language',
        text: tryValue(asset.language?.label),
        url: asset.language?.sameAs,
      },
      {
        icon: 'apartment',
        label: 'Publisher',
        text: tryValue(asset.publisher),
        url: tryUrl(asset.publisher),
      },
      {
        icon: 'bookmarks',
        label: 'Endpoint Documentation',
        text: tryValue(asset.endpointDocumentation),
        url: tryUrl(asset.endpointDocumentation),
      },
      {
        icon: 'gavel',
        label: 'Standard License',
        text: tryValue(asset.standardLicense),
        url: tryUrl(asset.standardLicense),
      },
    ];

    // MDS Specific Fields
    if (this.activeFeatureSet.hasMdsFields()) {
      fields.push(
        {
          icon: 'commute',
          label: 'Transport Mode',
          text: tryValue(asset.transportMode?.label),
        },
        {
          icon: 'commute',
          label: 'Data Category',
          text: tryValue(asset.dataCategory?.label),
        },
        {
          icon: 'commute',
          label: 'Data Subcategory',
          text: tryValue(asset.dataSubcategory?.label),
        },
        {
          icon: 'category',
          label: 'Data Model',
          text: tryValue(asset.dataModel),
          url: tryUrl(asset.dataModel),
          additionalClasses: 'break-all',
        },
      );
    }

    fields.push(
      {
        icon: 'link',
        label: 'Connector ID',
        text: tryValue(asset.originator),
        url: tryUrl(asset.originator),
      },
      {
        icon: 'account_circle',
        label: 'Organization',
        text: tryValue(asset.originatorOrganization),
      }
    );

    return fields;
  }
}
