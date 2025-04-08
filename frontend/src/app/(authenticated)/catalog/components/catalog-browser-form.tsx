/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import Link from 'next/link';
import {useRouter} from 'next/navigation';
import FormGroup from '@/components/form/form-group';
import InputField from '@/components/form/input-field';
import {Button} from '@/components/ui/button';
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {Form} from '@/components/ui/form';
import {
  type CatalogFormValue,
  useCatalogBrowserForm,
} from '@/app/(authenticated)/catalog/components/catalog-browser-form-schema';
import {urls} from '@/lib/urls';
import {type UiConfigPreconfiguredCounterparty} from '@sovity.de/edc-client';
import {useTranslations} from 'next-intl';

interface CatalogBrowserProps {
  preconfiguredCounterParties: UiConfigPreconfiguredCounterparty[];
}

export default function CatalogBrowserForm(props: CatalogBrowserProps) {
  const {form} = useCatalogBrowserForm();
  const router = useRouter();
  const t = useTranslations();

  const onSubmit = (values: CatalogFormValue) => {
    router.push(
      urls.catalog.listPage(values.participantId, values.connectorEndpoint),
    );
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-10">
        <FormGroup
          title={t('Pages.CatalogBrowser.counterpartyConnectorTitle')}
          subTitle={t('Pages.CatalogBrowser.counterpartyConnectorDescription')}>
          <>
            <InputField
              control={form.control}
              name="connectorEndpoint"
              label={t('General.connectorEndpoint')}
              placeholder="https://other-connector.dev/control/api/v1/dsp"
            />
            <InputField
              control={form.control}
              name="participantId"
              label={t('General.participantId')}
              placeholder="BPNL000000000001"
            />
          </>
        </FormGroup>
        <FormGroup
          title={t('Pages.CatalogBrowser.preconfiguredCounterpartiesTitle')}
          subTitle={t(
            'Pages.CatalogBrowser.preconfiguredCounterpartiesDescription',
          )}>
          <div className="grid gap-3 md:grid-cols-3">
            {props.preconfiguredCounterParties.map((value) => (
              <Link
                key={value.participantId}
                href={urls.catalog.listPage(
                  value.participantId,
                  value.connectorEndpoint,
                )}
                className={'flex items-stretch'}>
                <Card
                  className="cursor-pointer text-sm hover:bg-accent"
                  key={value.connectorEndpoint}>
                  <CardHeader>
                    <CardTitle className="text-base">
                      {value.participantId}
                    </CardTitle>
                    <CardDescription className={'text-xs'}>
                      {value.connectorEndpoint}
                    </CardDescription>
                  </CardHeader>
                </Card>
              </Link>
            ))}
          </div>
        </FormGroup>
        <div className="flex justify-end">
          <Button
            dataTestId={'btn-submit'}
            type="submit"
            disabled={form.formState.isSubmitting || !form.formState.isValid}
            isLoading={form.formState.isSubmitting}>
            Fetch Catalog
          </Button>
        </div>
      </form>
    </Form>
  );
}
