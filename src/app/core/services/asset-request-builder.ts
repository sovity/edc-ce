import {Injectable} from '@angular/core';
import {UiAssetCreateRequest, UiAssetEditRequest} from '@sovity.de/edc-client';
import {AssetEditorDialogFormValue} from '../../routes/connector-ui/asset-page/asset-edit-dialog/form/model/asset-editor-dialog-form-model';
import {toGmtZeroHourDate} from '../utils/date-utils';
import {AssetDataSourceMapper} from './asset-data-source-mapper';
import {AssetRequestCommonMetadata} from './asset-request-common-metadata';

@Injectable()
export class AssetRequestBuilder {
  constructor(private assetDataSourceMapper: AssetDataSourceMapper) {}

  buildAssetCreateRequest(
    formValue: AssetEditorDialogFormValue,
  ): UiAssetCreateRequest {
    const id = formValue.metadata?.id!;
    const metadata = this.buildAssetRequestCommonMetadata(formValue);
    const dataSource = this.assetDataSourceMapper.buildDataSource(
      formValue.datasource!,
    );
    return {
      id,
      ...metadata,
      dataSource,
    };
  }

  buildAssetEditRequest(
    formValue: AssetEditorDialogFormValue,
  ): UiAssetEditRequest {
    const metadata = this.buildAssetRequestCommonMetadata(formValue);
    const dataSourceOrNull = this.assetDataSourceMapper.buildDataSourceOrNull(
      formValue.datasource!,
    );
    return {
      ...metadata,
      dataSourceOverrideOrNull: dataSourceOrNull ?? undefined,
    };
  }

  buildAssetRequestCommonMetadata(
    formValue: AssetEditorDialogFormValue,
  ): AssetRequestCommonMetadata {
    const title = formValue.metadata?.title!;
    const version = formValue.metadata?.version;
    const description = formValue.metadata?.description;
    const language = formValue.metadata?.language?.id;
    const keywords = formValue.metadata?.keywords;
    const licenseUrl = formValue.metadata?.standardLicense;
    const publisherHomepage = formValue.metadata?.publisher;
    const mediaType = formValue.metadata?.contentType;
    const landingPageUrl = formValue.metadata?.endpointDocumentation;

    const dataCategory = formValue.advanced?.dataCategory?.id;
    const dataSubcategory = formValue.advanced?.dataSubcategory?.id;
    const transportMode = formValue.advanced?.transportMode?.id;
    const geoReferenceMethod = formValue.advanced?.geoReferenceMethod;
    const dataModel = formValue.advanced?.dataModel;

    const sovereignLegalName = formValue.advanced?.sovereignLegalName;
    const geoLocation = formValue.advanced?.geoLocation;
    const nutsLocations = formValue.advanced?.nutsLocations;
    const dataSampleUrls = formValue.advanced?.dataSampleUrls;
    const referenceFileUrls = formValue.advanced?.referenceFileUrls;
    const referenceFilesDescription =
      formValue.advanced?.referenceFilesDescription;
    const conditionsForUse = formValue.advanced?.conditionsForUse;
    const dataUpdateFrequency = formValue.advanced?.dataUpdateFrequency;
    let temporalCoverageFrom =
      formValue.advanced?.temporalCoverage?.from || undefined;
    let temporalCoverageToInclusive =
      formValue.advanced?.temporalCoverage?.toInclusive || undefined;
    temporalCoverageFrom = temporalCoverageFrom
      ? toGmtZeroHourDate(temporalCoverageFrom)
      : undefined;
    temporalCoverageToInclusive = temporalCoverageToInclusive
      ? toGmtZeroHourDate(temporalCoverageToInclusive)
      : undefined;

    return {
      title,
      language,
      description,
      publisherHomepage,
      licenseUrl,
      version,
      keywords,
      mediaType,
      landingPageUrl,
      dataCategory,
      dataSubcategory,
      dataModel,
      geoReferenceMethod,
      transportMode,
      sovereignLegalName,
      geoLocation,
      nutsLocations,
      dataSampleUrls,
      referenceFileUrls,
      referenceFilesDescription,
      conditionsForUse,
      dataUpdateFrequency,
      temporalCoverageFrom,
      temporalCoverageToInclusive,
      customJsonAsString: undefined,
      customJsonLdAsString: undefined,
      privateCustomJsonAsString: undefined,
      privateCustomJsonLdAsString: undefined,
    };
  }
}
