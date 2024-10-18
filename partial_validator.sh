#!/usr/bin/env bash

gradle \
    :extensions:contract-termination:test \
    --tests 'de.sovity.edc.extension.contacttermination.query.TerminateContractQueryTest' \
    :extensions:edc-ui-config:test \
    :extensions:last-commit-info:test \
    :extensions:policy-always-true:test \
    :extensions:policy-referring-connector:test \
    :extensions:sovity-messenger:test \
    :extensions:wrapper:wrapper-common-mappers:test \
    :utils:catalog-parser:test \
    :tests:test \
    --tests 'de.sovity.edc.e2e.ApiWrapperDemoTest' \
    --tests 'de.sovity.edc.e2e.UseCaseApiWrapperTest' \
    --tests 'de.sovity.edc.e2e.UiApiWrapperTest.retrieveSingleContractAgreement' \
