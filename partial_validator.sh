#!/usr/bin/env bash

gradle \
    :extensions:test \
    :utils:catalog-parser:test \
    :tests:test \
    --tests 'de.sovity.edc.e2e.ApiWrapperDemoTest' \
    --tests 'de.sovity.edc.e2e.AssetApiServiceTest' \
    --tests 'de.sovity.edc.e2e.CatalogApiTest' \
    --tests 'de.sovity.edc.e2e.ContractAgreementTerminationDetailsQueryTest' \
    --tests 'de.sovity.edc.e2e.DataSourceQueryParamsTest' \
    --tests 'de.sovity.edc.e2e.ManagementApiTransferTest' \
    --tests 'de.sovity.edc.e2e.PlaceholderDataSourceExtensionTest' \
    --tests 'de.sovity.edc.e2e.PolicyDefinitionApiServiceTest' \
    --tests 'de.sovity.edc.e2e.UiApiWrapperTest.retrieveSingleContractAgreement' \
    --tests 'de.sovity.edc.e2e.UseCaseApiWrapperTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreement.ContractAgreementPageTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreement.ContractAgreementTransferApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.TransferRequestBuilderTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionPageApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.DashboardPageApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferHistoryPageApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessAssetApiServiceTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.usecase.KpiApiTest' \
    --tests 'de.sovity.edc.ext.wrapper.api.usecase.SupportedPolicyApiTest' \
    --tests 'de.sovity.edc.extension.contacttermination.CanGetAgreementPageForNonTerminatedContractTest' \
    --tests 'de.sovity.edc.extension.contacttermination.CanGetAgreementPageForTerminatedContractTest' \
    --tests 'de.sovity.edc.extension.contacttermination.CanTerminateFromConsumerTest' \
    --tests 'de.sovity.edc.extension.contacttermination.ContractTerminationTest' \
    --tests 'de.sovity.edc.extension.contacttermination.ContractTerminationTestUtils' \

# AlwaysTrueMigrationReversedTest
# AlwaysTrueMigrationTest -> no migration of contracts? -> old contract kick out
# Ordering -> skip
# Params -> skip

# Can normal transfer happen if using old contracts?
# Can take old data offer and negotiate with the old offer?
# Ask Kamil for example policy for catena
