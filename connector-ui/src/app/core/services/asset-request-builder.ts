import {Injectable} from '@angular/core';
import {UiAssetCreateRequest, UiAssetEditRequest} from '@sovity.de/edc-client';
import {EditAssetFormValue} from 'src/app/shared/business/edit-asset-form/form/model/edit-asset-form-model';
import {toGmtZeroHourDate} from '../utils/date-utils';
import {AssetDataSourceMapper} from './asset-data-source-mapper';
import {AssetRequestCommonMetadata} from './asset-request-common-metadata';

@Injectable()
export class AssetRequestBuilder {
  constructor(private assetDataSourceMapper: AssetDataSourceMapper) {}

  buildAssetCreateRequest(formValue: EditAssetFormValue): UiAssetCreateRequest {
    const id = formValue.general?.id!;
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

  buildAssetEditRequest(formValue: EditAssetFormValue): UiAssetEditRequest {
    const metadata = this.buildAssetRequestCommonMetadata(formValue);
    const dataSourceOrNull = this.assetDataSourceMapper.buildDataSourceOrNull(
      formValue.datasource,
    );
    return {
      ...metadata,
      dataSourceOverrideOrNull: dataSourceOrNull ?? undefined,
    };
  }

  buildAssetRequestCommonMetadata(
    formValue: EditAssetFormValue,
  ): AssetRequestCommonMetadata {
    const title = formValue.general?.name!;
    const version = formValue.general?.version;
    const description = formValue.general?.description;
    const language = formValue.general?.language?.id;
    const keywords = formValue.general?.keywords;
    const licenseUrl = formValue.general?.standardLicense;
    const publisherHomepage = formValue.general?.publisher;
    const mediaType = formValue.general?.contentType;
    const landingPageUrl = formValue.general?.endpointDocumentation;
    const dataCategory = formValue.general?.dataCategory?.id;
    const dataSubcategory = formValue.general?.dataSubcategory?.id;

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
