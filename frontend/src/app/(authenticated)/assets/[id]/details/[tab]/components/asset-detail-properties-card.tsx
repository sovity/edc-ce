/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import CopyTextButton from '@/components/copy-text-button';
import ExternalLink from '@/components/links/external-link';
import ScrollableDialogButton from '@/components/scrollable-dialog/scrollable-dialog-button';
import {Separator} from '@/components/ui/separator';
import {buildHttpDatasourceParametrizationHints} from '@/lib/utils/assets/http-datasource';
import {getLanguageSelectItemById} from '@/lib/utils/assets/language-select-item-service';
import {getTemporalCoverageStr} from '@/lib/utils/assets/temporal-coverage';
import {recordToList} from '@/lib/utils/object-utils';
import {
  BookAIcon,
  BookTextIcon,
  BoxIcon,
  Building2Icon,
  BuildingIcon,
  CalendarIcon,
  EclipseIcon,
  FileBoxIcon,
  FileCodeIcon,
  FileText,
  FileTypeIcon,
  GitBranchIcon,
  Globe,
  HouseIcon,
  IdCardIcon,
  LinkIcon,
  LocateFixed,
  LocateIcon,
  MailIcon,
  MapPin,
  PaperclipIcon,
  RotateCwIcon,
  ScaleIcon,
  SectionIcon,
  TextIcon,
  VariableIcon,
} from 'lucide-react';
import {useTranslations} from 'next-intl';
import {z} from 'zod';
import {type UiAssetProps} from './asset-detail-overview-card';
import {AssetProperty} from './asset-property';
import {AssetPropertyCard} from './asset-property-card';
import {buildMailtoUrl} from '@/lib/utils/mailto-link-utils';

const parseJsonToRecord = (
  jsonString: string | undefined,
): Record<string, string> => {
  const parsed: unknown = JSON.parse(jsonString ?? '{}');
  return z.record(z.string()).parse(parsed);
};

export const AssetDetailPropertiesCard = ({data}: UiAssetProps) => {
  const t = useTranslations();

  const customPropertiesObj = parseJsonToRecord(data.customJsonAsString);
  const customLdPropertiesObj = parseJsonToRecord(data.customJsonLdAsString);
  const privateCustomPropertiesObj = parseJsonToRecord(
    data.privateCustomJsonAsString,
  );
  const privateCustomLdPropertiesObj = parseJsonToRecord(
    data.privateCustomJsonLdAsString,
  );

  const customProperties = recordToList(customPropertiesObj);
  const customLdProperties = recordToList(customLdPropertiesObj);
  const privateCustomProperties = recordToList(privateCustomPropertiesObj);
  const privateCustomLdProperties = recordToList(privateCustomLdPropertiesObj);

  const showAdditionalProperties =
    data.dataCategory ||
    data.dataSubcategory ||
    data.dataModel ||
    data.geoReferenceMethod ||
    data.geoLocation ||
    data.nutsLocations ||
    data.sovereignLegalName ||
    data.dataSampleUrls ||
    data.referenceFileUrls ||
    data.conditionsForUse ||
    data.dataUpdateFrequency ||
    data.temporalCoverageFrom ||
    data.temporalCoverageToInclusive;

  const showCustomProperties =
    customLdProperties.length || customProperties.length;

  const showPrivateProperties =
    privateCustomLdProperties.length || privateCustomProperties.length;

  const showEmailContact =
    data.onRequestContactEmail || data.onRequestContactEmailSubject;

  return (
    <div className="space-y-4">
      <AssetPropertyCard title={t('General.properties')}>
        <AssetProperty Icon={FileText} label="Id" value={data.assetId} />

        <AssetProperty
          Icon={GitBranchIcon}
          label={t('General.version')}
          value={data.version}
        />

        {data.language ? (
          <AssetProperty
            Icon={Globe}
            label={t('General.language')}
            value={getLanguageSelectItemById(data.language).label}
          />
        ) : null}

        {data.publisherHomepage ? (
          <AssetProperty
            Icon={HouseIcon}
            label={t('General.publisher')}
            value={
              <ExternalLink
                href={data.publisherHomepage}
                noIcon
                showReferrer
                whitespaceNormal>
                {data.publisherHomepage}
              </ExternalLink>
            }
          />
        ) : null}

        {data.landingPageUrl ? (
          <AssetProperty
            Icon={BookTextIcon}
            label={t('General.endpointDocumentation')}
            value={
              <ExternalLink
                href={data.landingPageUrl}
                noIcon
                showReferrer
                whitespaceNormal>
                {data.landingPageUrl}
              </ExternalLink>
            }
          />
        ) : null}

        {data.licenseUrl ? (
          <AssetProperty
            Icon={ScaleIcon}
            label={t('General.license')}
            value={
              <ExternalLink
                href={data.licenseUrl}
                noIcon
                showReferrer
                whitespaceNormal>
                {data.licenseUrl}
              </ExternalLink>
            }
          />
        ) : null}

        <AssetProperty
          Icon={IdCardIcon}
          label={t('General.participantId')}
          value={data.participantId}
        />

        <AssetProperty
          Icon={BuildingIcon}
          label={t('General.organization')}
          value={data.creatorOrganizationName}
        />

        <AssetProperty
          Icon={LinkIcon}
          label={t('General.connectorEndpoint')}
          value={
            <CopyTextButton
              dataTestId={'btn-copy-connector-endpoint'}
              value={data.connectorEndpoint}
              label={data.connectorEndpoint}
              variant="outline"
              className="mt-1 px-3 py-2"
            />
          }
        />

        <AssetProperty
          Icon={VariableIcon}
          label={t('General.httpDataSourceParameterization')}
          value={buildHttpDatasourceParametrizationHints(data)}
        />

        <AssetProperty
          Icon={FileTypeIcon}
          label={t('General.contentType')}
          value={data.mediaType}
        />
      </AssetPropertyCard>

      {showAdditionalProperties ? (
        <AssetPropertyCard title={t('General.additionalProperties')}>
          <AssetProperty
            Icon={BoxIcon}
            label={t('General.dataCategory')}
            value={data.dataCategory}
          />

          <AssetProperty
            Icon={FileBoxIcon}
            label={t('General.dataSubcategory')}
            value={data.dataSubcategory}
          />

          <AssetProperty
            Icon={EclipseIcon}
            label={t('General.dataModel')}
            value={data.dataModel}
          />

          <AssetProperty
            Icon={LocateFixed}
            label={t('General.geoReferenceMethod')}
            value={data.geoReferenceMethod}
          />

          <AssetProperty
            Icon={MapPin}
            label={t('General.geoLocation')}
            value={data.geoLocation}
          />

          <AssetProperty
            Icon={LocateIcon}
            label={t('General.nutsLocations')}
            value={data.nutsLocations?.join(', ')}
          />

          <AssetProperty
            Icon={Building2Icon}
            label={t('General.sovereign')}
            value={data.sovereignLegalName}
          />

          {data.dataSampleUrls ? (
            <AssetProperty
              Icon={PaperclipIcon}
              label={t('General.dataSamples')}
              value={
                <div className="mt-1">
                  <ScrollableDialogButton
                    dialogId={`data-samples-dialog-${data.assetId}`}
                    data={{
                      title: t('General.dataSamples'),
                      subtitle: data.title,
                      content: (
                        <>
                          {data.dataSampleUrls?.map((x) => (
                            <div key={x}>
                              <ExternalLink href={x} showReferrer>
                                {x}
                              </ExternalLink>
                            </div>
                          ))}
                        </>
                      ),
                    }}
                    buttonText={t('Pages.AssetDetails.showDataSamples')}
                  />
                </div>
              }
            />
          ) : null}

          {data.referenceFileUrls ? (
            <AssetProperty
              Icon={BookAIcon}
              label={t('General.referenceFiles')}
              value={
                <div className="mt-1">
                  <ScrollableDialogButton
                    dialogId={`reference-files-dialog-${data.assetId}`}
                    data={{
                      title: t('General.referenceFiles'),
                      subtitle: data.title,
                      content: (
                        <>
                          {data.referenceFilesDescription ? (
                            <div>
                              {data.referenceFilesDescription}
                              <Separator className="my-2" />
                            </div>
                          ) : null}
                          {data.referenceFileUrls?.map((x) => (
                            <div key={x}>
                              <ExternalLink href={x} showReferrer>
                                {x}
                              </ExternalLink>
                            </div>
                          ))}
                        </>
                      ),
                    }}
                    buttonText={t('Pages.AssetDetails.showReferenceFiles')}
                  />
                </div>
              }
            />
          ) : null}

          {data.conditionsForUse ? (
            <AssetProperty
              Icon={SectionIcon}
              label={t('General.conditionsForUse')}
              value={
                <div className="mt-1">
                  <ScrollableDialogButton
                    dialogId={`conditions-for-use-dialog-${data.assetId}`}
                    data={{
                      title: t('General.conditionsForUse'),
                      subtitle: data.title,
                      content: data.conditionsForUse,
                    }}
                    buttonText={t('Pages.AssetDetails.showConditionsForUse')}
                  />
                </div>
              }
            />
          ) : null}

          <AssetProperty
            Icon={RotateCwIcon}
            label={t('General.dataUpdateFrequency')}
            value={data.dataUpdateFrequency}
          />

          <AssetProperty
            Icon={CalendarIcon}
            label={t('General.temporalCoverage')}
            value={getTemporalCoverageStr(data)}
          />
        </AssetPropertyCard>
      ) : null}

      {showEmailContact ? (
        <AssetPropertyCard
          title={t('Pages.AssetDetails.contactInformation')}
          footer={
            data.onRequestContactEmail && (
              <ExternalLink
                variant={'default'}
                size={'default'}
                dataTestId={'btn-contact-open-mail-client'}
                href={buildMailtoUrl({
                  email: data.onRequestContactEmail,
                  subject: data.onRequestContactEmailSubject,
                })}>
                {t('General.openMailClient')}
              </ExternalLink>
            )
          }>
          {data.onRequestContactEmail ? (
            <AssetProperty
              Icon={MailIcon}
              label={t('General.contactEmail')}
              value={
                <CopyTextButton
                  dataTestId={'btn-copy-contact-email'}
                  value={data.onRequestContactEmail}
                  label={data.onRequestContactEmail}
                  variant="outline"
                  className="mt-1 px-3 py-2"
                />
              }
            />
          ) : null}

          {data.onRequestContactEmailSubject ? (
            <AssetProperty
              Icon={TextIcon}
              label={t('General.preferredEmailSubject')}
              value={
                <CopyTextButton
                  dataTestId={'btn-copy-contact-email-subject'}
                  value={data.onRequestContactEmailSubject}
                  label={data.onRequestContactEmailSubject}
                  variant="outline"
                  className="mt-1 px-3 py-2"
                />
              }
            />
          ) : null}
        </AssetPropertyCard>
      ) : null}

      {showCustomProperties ? (
        <AssetPropertyCard title={t('Pages.AssetDetails.customProperties')}>
          {customProperties.map((x) => (
            <AssetProperty
              key={x.key}
              Icon={FileCodeIcon}
              label={x.key}
              value={x.value}
            />
          ))}
          {customLdProperties.map((x) => (
            <AssetProperty
              key={x.key}
              Icon={FileCodeIcon}
              label={x.key}
              value={x.value}
            />
          ))}
        </AssetPropertyCard>
      ) : null}

      {showPrivateProperties ? (
        <AssetPropertyCard title={t('Pages.AssetDetails.privateProperties')}>
          {privateCustomProperties.map((x) => (
            <AssetProperty
              key={x.key}
              Icon={FileCodeIcon}
              label={x.key}
              value={x.value}
            />
          ))}
          {privateCustomLdProperties.map((x) => (
            <AssetProperty
              key={x.key}
              Icon={FileCodeIcon}
              label={x.key}
              value={x.value}
            />
          ))}
        </AssetPropertyCard>
      ) : null}
    </div>
  );
};
