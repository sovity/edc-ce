plugins {
    `java-library`
}

dependencies {
    // Control-Plane
    api(libs.edc.controlPlaneCore)
    api(libs.edc.managementApi)
    api(libs.edc.apiObservability)
    api(libs.edc.configurationFilesystem)
    api(libs.edc.controlPlaneAggregateServices)
    api(libs.edc.http)
    api(libs.edc.dsp)
    api(libs.edc.jsonLd)

    // Data Management API Key
    api(libs.edc.authTokenbased)

    // sovity Extensions Package
    api(project(":extensions:sovity-edc-extensions-package"))
    api(project(":extensions:postgres-flyway"))
    api(project(":extensions:transfer-process-status-checker"))

    // Control-plane to Data-plane
    api(libs.edc.transferDataPlane)
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.dataPlaneSelectorClient)

    // Data-plane
    api(libs.edc.dataPlaneHttp)
    api(libs.edc.dataPlaneFramework)
    api(libs.edc.dataPlaneCore)
    api(libs.edc.dataPlaneUtil)

    // Http Proxy Support
    api(libs.edc.dataPlaneApi)
    api(libs.edc.dataPlaneSelectorApi)
    api(libs.edc.transferPullHttpDynamicReceiver)
}

group = libs.versions.sovityEdcGroup.get()
