#!/usr/bin/env bash

gradle \
    :extensions:contract-termination:test \
    :extensions:edc-ui-config:test \
    :extensions:last-commit-info:test \
    :extensions:policy-always-true:test \
    :extensions:policy-referring-connector:test \
    :extensions:sovity-messenger:test \
    :extensions:wrapper:wrapper-common-mappers:test \
    :utils:catalog-parser:test \
    :tests:test \
    --tests 'de.sovity.edc.e2e.ApiWrapperDemoTest' \
    --tests 'de.sovity.edc.e2e.ContractAgreementTerminationDetailsQueryTest' \
    --tests 'de.sovity.edc.e2e.PolicyDefinitionApiServiceTest' \
    --tests 'de.sovity.edc.e2e.UiApiWrapperTest.retrieveSingleContractAgreement' \
    --tests 'de.sovity.edc.e2e.UseCaseApiWrapperTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreement.ContractAgreementPageTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreement.ContractAgreementTransferApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.TransferRequestBuilderTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionPageApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.DashboardPageApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessAssetApiServiceTest' \
