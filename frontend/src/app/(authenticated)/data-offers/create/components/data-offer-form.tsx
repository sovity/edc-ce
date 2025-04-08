/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import CheckboxField from '@/components/form/checkbox-field';
import ComboboxField from '@/components/form/combobox-field';
import {DateRangeField} from '@/components/form/date-range-field';
import FormGroup from '@/components/form/form-group';
import InputField from '@/components/form/input-field';
import RadioGroupField from '@/components/form/radio-group-field';
import {TagInputField} from '@/components/form/tag-input-field';
import TextareaField from '@/components/form/textarea-field';
import ValueListField from '@/components/form/value-list-field';
import PolicyEditor from '@/components/policy-editor/editor/policy-editor';
import {usePolicyEditor} from '@/components/policy-editor/editor/use-policy-editor';
import {sovityDataspacePolicyContext} from '@/components/policy-editor/supported-policies';
import {Button} from '@/components/ui/button';
import {Form} from '@/components/ui/form';
import {DataOfferLiveForm} from '@/app/(authenticated)/data-offers/create/components/data-offer-live-form';
import {DataOfferOnRequestForm} from '@/app/(authenticated)/data-offers/create/components/data-offer-on-request-form';
import {useDataOfferCreateForm} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-hook';
import {type DataOfferCreateFormModel} from '@/app/(authenticated)/data-offers/create/components/data-offer-form-schema';
import {languageSelectItemGroups} from '@/lib/utils/assets/language-select-item-service';
import {
  type DataOfferPublishType,
  type UiPolicyExpression,
} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';

export const DataOfferForm = ({
  mode,
  initialFormValue,
  onSubmit,
}: {
  mode: 'EDIT' | 'CREATE';
  initialFormValue: DataOfferCreateFormModel;
  onSubmit: (
    value: DataOfferCreateFormModel,
    expression: UiPolicyExpression,
  ) => Promise<unknown>;
}) => {
  const {form} = useDataOfferCreateForm(initialFormValue);
  const t = useTranslations();

  // Supported Policies
  const policyContext = sovityDataspacePolicyContext();
  const policyEditor = usePolicyEditor(
    policyContext,
    form,
    'publishing.policy',
  );

  async function onSubmitFn(values: DataOfferCreateFormModel) {
    await onSubmit(values, policyEditor.getUiPolicyExpression());
  }

  const formValue = form.watch();

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmitFn)} className="space-y-10">
        <FormGroup
          title={t('Pages.DataOfferCreate.data_offer_type')}
          subTitle={t('Pages.DataOfferCreate.define_data_offer')}>
          {/* Data offer Type */}
          <RadioGroupField
            control={form.control}
            name={'type.offerType'}
            label={t('Pages.DataOfferCreate.offer_type')}
            items={[
              ...(mode === 'EDIT'
                ? [
                    {
                      id: 'UNCHANGED',
                      label: t('Pages.DataOfferCreate.unchanged'),
                    },
                  ]
                : []),
              {
                id: 'LIVE',
                label: t('Pages.DataOfferCreate.readily_available'),
              },
              {
                id: 'ON_REQUEST',
                label: t('Pages.DataOfferCreate.on_request'),
              },
            ]}
          />

          <DataOfferOnRequestForm form={form} formKeyDataOfferType={'type'} />
          <DataOfferLiveForm form={form} formKeyDataOfferType={'type'} />
        </FormGroup>
        <FormGroup
          title={t('Pages.DataOfferCreate.general_information')}
          subTitle={t('Pages.DataOfferCreate.general_information_description')}>
          {/* Title */}
          <InputField
            control={form.control}
            name="general.title"
            placeholder={t('Pages.DataOfferCreate.title_placeholder')}
            label={t('Pages.DataOfferCreate.title_label')}
            tooltip={t('Pages.DataOfferCreate.title_tooltip')}
            isRequired
          />

          {/* Asset ID */}
          <InputField
            control={form.control}
            name="general.assetId"
            placeholder={t('Pages.DataOfferCreate.asset_id_label')}
            label={t('Pages.DataOfferCreate.asset_id_label')}
            tooltip={t('Pages.DataOfferCreate.asset_id_tooltip')}
            isRequired
          />

          {/* Description */}
          <TextareaField
            control={form.control}
            name="general.description"
            placeholder={
              '# My Asset\n\nAt vero eos et accusam et justo duo dolores et ea rebum.\n\n## Details\n\nAt vero eos et accusam et justo duo dolores et ea **rebum**.'
            }
            label={t('Pages.DataOfferCreate.description')}
            tooltip={t('Pages.DataOfferCreate.description_uses')}
          />

          {/* Keywords */}
          <TagInputField
            control={form.control}
            name="general.keywords"
            label={t('Pages.DataOfferCreate.keywords')}
            placeholder={t('General.addKeyword')}
          />

          {/* Show Advanced Fields */}
          {mode === 'CREATE' && (
            <CheckboxField
              control={form.control}
              name="advanced.showAdvancedFields"
              label={t('General.advancedFields')}
              item={{
                id: 'showAdvancedFields',
                label: t('Pages.DataOfferCreate.show_advanced_fields'),
              }}
            />
          )}

          {formValue.advanced.showAdvancedFields && (
            <>
              {/* Version */}
              <InputField
                control={form.control}
                name="advanced.version"
                placeholder="1.0.0"
                label={t('Pages.DataOfferCreate.version')}
                tooltip={t('Pages.DataOfferCreate.version_tooltip')}
              />

              {/* Language */}
              <ComboboxField
                control={form.control}
                name="advanced.language"
                label={t('Pages.DataOfferCreate.language')}
                itemGroups={languageSelectItemGroups()}
                selectPlaceholder={t('Pages.DataOfferCreate.select_language')}
                searchPlaceholder={t('General.searchPlaceholder')}
                searchEmptyMessage={t('General.noResults')}
              />
            </>
          )}
        </FormGroup>
        {formValue.advanced.showAdvancedFields && (
          <>
            <FormGroup
              title={t('Pages.DataOfferCreate.documentation')}
              subTitle={t(
                'Pages.DataOfferCreate.context_information_description',
              )}>
              {/* Endpoint Documentation */}
              <InputField
                control={form.control}
                name="advanced.endpointDocumentation"
                placeholder="https://"
                label={t('Pages.DataOfferCreate.endpoint_documentation')}
                tooltip={t(
                  'Pages.DataOfferCreate.endpoint_documentation_tooltip',
                )}
              />

              {/* Content Type */}
              <InputField
                control={form.control}
                name="advanced.contentType"
                placeholder="application/json"
                label={t('Pages.DataOfferCreate.content_type')}
              />

              {/* Data Samples */}
              <ValueListField
                control={form.control}
                name="advanced.dataSampleUrls"
                label={t('Pages.DataOfferCreate.data_samples')}
                placeholder="https://example.com/data/samples"
                tooltip={t('Pages.DataOfferCreate.data_samples_tooltip')}
              />

              {/* Reference files */}
              <ValueListField
                control={form.control}
                name="advanced.referenceFileUrls"
                label={t('Pages.DataOfferCreate.reference_files')}
                placeholder="https://example.com/references/file"
                tooltip={t('Pages.DataOfferCreate.reference_files_tooltip')}
              />

              {/* Reference files description */}
              {formValue.advanced?.referenceFileUrls?.length !== undefined &&
                formValue.advanced?.referenceFileUrls?.length > 0 && (
                  <TextareaField
                    control={form.control}
                    name="advanced.referenceFilesDescription"
                    label={t(
                      'Pages.DataOfferCreate.reference_files_description_label',
                    )}
                    tooltip={t(
                      'Pages.DataOfferCreate.reference_files_description',
                    )}
                    placeholder={
                      '# My Asset\n\nAt vero eos et accusam et justo duo dolores et ea rebum.\n\n## Details\n\nAt vero eos et accusam et justo duo dolores et ea **rebum**.'
                    }
                  />
                )}
            </FormGroup>
            <FormGroup
              title={t('Pages.DataOfferCreate.location_time_title')}
              subTitle={t('Pages.DataOfferCreate.location_time_description')}>
              {/* Temporal coverage */}
              <DateRangeField
                control={form.control}
                name="advanced.temporalCoverage"
                label={t('Pages.DataOfferCreate.temporal_coverage')}
              />

              {/* Data update frequency */}
              <InputField
                control={form.control}
                name="advanced.dataUpdateFrequency"
                label={t('Pages.DataOfferCreate.data_update_frequency_label')}
                placeholder={t(
                  'Pages.DataOfferCreate.data_update_frequency_placeholder',
                )}
                tooltip={t(
                  'Pages.DataOfferCreate.data_update_frequency_tooltip',
                )}
              />
            </FormGroup>
            <FormGroup
              title={t('Pages.DataOfferCreate.legal_information_title')}
              subTitle={t(
                'Pages.DataOfferCreate.legal_information_description',
              )}>
              {/* Sovereign legal name */}
              <InputField
                control={form.control}
                name="advanced.sovereignLegalName"
                placeholder={t('Pages.DataOfferCreate.sovereign_placeholder')}
                label={t('Pages.DataOfferCreate.sovereign_label')}
                tooltip={t('Pages.DataOfferCreate.sovereign_tooltip')}
              />

              {/* Publisher */}
              <InputField
                control={form.control}
                name="advanced.publisher"
                placeholder="https://example.com"
                label={t('Pages.DataOfferCreate.publisher_label')}
                tooltip={t('Pages.DataOfferCreate.publisher_tooltip')}
              />

              {/* Standard License */}
              <InputField
                control={form.control}
                name="advanced.standardLicense"
                placeholder="https://example.com/license"
                label={t('Pages.DataOfferCreate.standard_license_label')}
                tooltip={t('Pages.DataOfferCreate.standard_license_tooltip')}
              />

              {/* Conditions for use */}
              <TextareaField
                control={form.control}
                name="advanced.conditionsForUse"
                label={t('Pages.DataOfferCreate.conditions_for_use_label')}
                tooltip={t(
                  'Pages.DataOfferCreate.conditions_for_use_description_hint',
                )}
                placeholder={
                  '# My Asset\n\nAt vero eos et accusam et justo duo dolores et ea rebum.\n\n## Details\n\nAt vero eos et accusam et justo duo dolores et ea **rebum**.'
                }></TextareaField>
            </FormGroup>
          </>
        )}

        {/* Publish Mode */}
        {mode === 'CREATE' && (
          <FormGroup
            title={t('Pages.DataOfferCreate.publishing')}
            subTitle={t('Pages.DataOfferCreate.publishing_description')}>
            <RadioGroupField
              control={form.control}
              name={'publishing.mode'}
              label={t('Pages.DataOfferCreate.publishing_mode_label')}
              items={[
                {
                  id: 'PUBLISH_UNRESTRICTED' satisfies DataOfferPublishType,
                  label: t('Pages.DataOfferCreate.publish_unrestricted'),
                  description: t(
                    'Pages.DataOfferCreate.publish_unrestricted_tooltip',
                  ),
                },
                {
                  id: 'PUBLISH_RESTRICTED' satisfies DataOfferPublishType,
                  label: t('Pages.DataOfferCreate.publish_restricted'),
                  description: t(
                    'Pages.DataOfferCreate.publish_restricted_tooltip',
                  ),
                },
                {
                  id: 'DONT_PUBLISH' satisfies DataOfferPublishType,
                  label: t('Pages.DataOfferCreate.publish_asset_only'),
                  description: t(
                    'Pages.DataOfferCreate.publish_asset_only_tooltip',
                  ),
                },
              ]}
            />

            {/* Policy Expression */}
            {formValue.publishing.mode === 'PUBLISH_RESTRICTED' && (
              <PolicyEditor policyEditor={policyEditor} />
            )}
          </FormGroup>
        )}

        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            {mode === 'EDIT'
              ? t('General.edit')
              : formValue.publishing.mode === 'DONT_PUBLISH'
                ? t('Pages.DataOfferCreate.submitCreate')
                : t('Pages.DataOfferCreate.submitPublish')}
          </Button>
        </div>
      </form>
    </Form>
  );
};
