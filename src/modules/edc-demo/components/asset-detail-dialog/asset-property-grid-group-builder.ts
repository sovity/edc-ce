import {Injectable} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ActiveFeatureSet} from '../../../app/config/active-feature-set';
import {Policy} from '../../../edc-dmgmt-client';
import {Asset} from '../../models/asset';
import {JsonDialogComponent} from '../json-dialog/json-dialog.component';
import {JsonDialogData} from '../json-dialog/json-dialog.data';
import {PropertyGridGroup} from '../property-grid-group/property-grid-group';
import {PropertyGridField} from '../property-grid/property-grid-field';
import {PropertyGridUtils} from '../property-grid/property-grid-utils';

@Injectable()
export class AssetPropertyGridGroupBuilder {
  constructor(
    private matDialog: MatDialog,
    private activeFeatureSet: ActiveFeatureSet,
    private propertyGridUtils: PropertyGridUtils,
  ) {}

  buildPropertyGridGroups(
    asset: Asset,
    policy: Policy | undefined,
  ): PropertyGridGroup[] {
    let fieldGroups = [
      this.buildDefaultPropertiesGroup(asset),
      this.buildAdditionalPropertiesGroup(asset),
    ];

    if (policy) {
      fieldGroups.push({
        groupLabel: 'Policies',
        properties: [
          {
            icon: 'policy',
            label: 'Contract Policy',
            text: 'Show Policy Details',
            onclick: () => this.onShowPolicyDetailsClick(asset, policy),
          },
        ],
      });
    }

    return fieldGroups.filter((it) => it.properties.length);
  }

  private buildDefaultPropertiesGroup(asset: Asset): PropertyGridGroup {
    const fields: PropertyGridField[] = [
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
        icon: 'link',
        label: 'Connector Endpoint',
        ...this.propertyGridUtils.guessValue(asset.originator),
      },
      {
        icon: 'account_circle',
        label: 'Organization',
        ...this.propertyGridUtils.guessValue(asset.originatorOrganization),
      },
      {
        icon: 'category',
        label: 'Content Type',
        ...this.propertyGridUtils.guessValue(asset.contentType),
      },
    ];

    if (this.activeFeatureSet.hasMdsFields()) {
      fields.push(...this.buildMdsProperties(asset, true));
    }

    return {
      groupLabel: null,
      properties: fields,
    };
  }

  private buildAdditionalPropertiesGroup(asset: Asset): PropertyGridGroup {
    const fields: PropertyGridField[] = [];

    if (!this.activeFeatureSet.hasMdsFields()) {
      fields.push(...this.buildMdsProperties(asset, false));
    }

    fields.push(
      ...asset.additionalProperties.map((prop) => {
        return {
          icon: 'category ',
          label: prop.key,
          ...this.propertyGridUtils.guessValue(prop.value),
        };
      }),
    );

    return {
      groupLabel: 'Additional Properties',
      properties: fields,
    };
  }

  private buildMdsProperties(
    asset: Asset,
    includeEmpty: boolean,
  ): PropertyGridField[] {
    const fields: PropertyGridField[] = [];
    if (includeEmpty || asset.transportMode) {
      fields.push({
        icon: 'commute',
        label: 'Transport Mode',
        ...this.propertyGridUtils.guessValue(asset.transportMode?.label),
      });
    }
    if (includeEmpty || asset.dataCategory) {
      fields.push({
        icon: 'commute',
        label: 'Data Category',
        ...this.propertyGridUtils.guessValue(asset.dataCategory?.label),
      });
    }
    if (includeEmpty || asset.dataSubcategory) {
      fields.push({
        icon: 'commute',
        label: 'Data Subcategory',
        ...this.propertyGridUtils.guessValue(asset.dataSubcategory?.label),
      });
    }
    if (includeEmpty || asset.dataModel) {
      fields.push({
        icon: 'category',
        label: 'Data Model',
        ...this.propertyGridUtils.guessValue(asset.dataModel),
      });
    }
    if (includeEmpty || asset.geoReferenceMethod) {
      fields.push({
        icon: 'commute',
        label: 'Geo Reference Method',
        ...this.propertyGridUtils.guessValue(asset.geoReferenceMethod),
      });
    }
    return fields;
  }

  private onShowPolicyDetailsClick(asset: Asset, policyDetails: Policy) {
    const data: JsonDialogData = {
      title: 'Contract Policy',
      subtitle: asset.name,
      icon: 'policy',
      objectForJson: policyDetails,
    };
    this.matDialog.open(JsonDialogComponent, {data});
  }
}
