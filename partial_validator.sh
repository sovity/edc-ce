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
    --tests 'de.sovity.edc.e2e.ContractAgreementPageTest' \
    --tests 'de.sovity.edc.e2e.ContractAgreementTerminationDetailsQueryTest' \
    --tests 'de.sovity.edc.e2e.ContractAgreementTransferApiServiceTest' \
    --tests 'de.sovity.edc.e2e.TransferRequestBuilderTest' \
    --tests 'de.sovity.edc.e2e.UseCaseApiWrapperTest' \
    --tests 'de.sovity.edc.e2e.UiApiWrapperTest.retrieveSingleContractAgreement' \
