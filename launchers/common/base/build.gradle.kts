plugins {
    `java-library`
}

dependencies {
    // For Custom Launcher Code
    implementation(project(":config"))

    // // Management API Key
    api(libs.edc.authTokenbased)

    // // sovity Extensions Package
    api(project(":extensions:sovity-edc-extensions-package"))
    api(project(":extensions:postgres-flyway"))

    // Control Plane
    implementation(libs.edc.jsonLd)
    implementation(libs.edc.connectorCore)
    implementation(libs.edc.policyMonitorCore)
    implementation(libs.edc.validatorDataAddressHttpData)
    implementation(libs.edc.callbackEventDispatcher)
    implementation(libs.edc.callbackHttpDispatcher)
    implementation(libs.edc.callbackStaticEndpoint)

    implementation(libs.edc.controlPlaneCore)
    implementation(libs.edc.managementApi)
    implementation(libs.edc.secretsApi)
    implementation(libs.edc.controlApiConfiguration)
    implementation(libs.edc.controlPlaneApi)
    implementation(libs.edc.http)
    implementation(libs.edc.dsp)
    implementation(libs.edc.configurationFilesystem)

    // data-plane-selector
    implementation(libs.edc.dataPlaneSelectorCore)
    implementation(libs.edc.dataPlaneSelectorApi)

    // Data Transfer
    implementation(libs.edc.transferPullHttpDynamicReceiver)
    implementation(libs.edc.edrStoreCore)
    implementation(libs.edc.edrStoreReceiver)
    implementation(libs.tractus.transferDataplaneSignaling)
    implementation(libs.tractus.azblobProvisioner)
    // fix CVE in Catalog, see: https://github.com/eclipse-tractusx/tractusx-edc/pull/1584
    implementation(libs.tractus.datasetBugfix)

    // Integrated Data Plane
    implementation(libs.edc.webSpi)
    implementation(libs.edc.dataPlaneCore)
    // normally the pipeline service is provided by the data-plane-core dependency
    // however there is a TX version, which contains some fixes
    // see: https://github.com/eclipse-tractusx/tractusx-edc/pull/1520
    implementation(libs.tractus.pipelineService)
    implementation(libs.edc.dataPlaneControlApi)
    implementation(libs.edc.dataPlanePublicApiV2)
    implementation(libs.edc.controlPlaneApiClient)
    implementation(project(":extensions:integrated-data-plane-initializer"))
    implementation(libs.edc.dataPlaneSignalingApi)

    // transfer-types
    implementation(libs.edc.dataPlaneHttp)
    implementation(libs.edc.dataPlaneHttpOauth2)
    implementation(libs.edc.azure.dataPlaneAzureStorage)
    implementation(libs.edc.aws.dataPlaneAwsS3)

    // persistence-db
    implementation(libs.edc.dataPlaneStoreSql)
    implementation(libs.edc.accesstokendataStoreSql)
}

group = libs.versions.sovityEdcGroup.get()
