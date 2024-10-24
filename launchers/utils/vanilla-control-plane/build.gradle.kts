
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    // EDC Base
    api(libs.edc.jsonLd)
    api(libs.edc.connectorCore)
    api(libs.edc.policyMonitorCore)

    // EDC CP
    api(libs.edc.controlPlaneCore)
    api(libs.edc.managementApi)
    api(libs.edc.controlApiConfiguration)
    api(libs.edc.controlPlaneApi)
    api(libs.edc.http)
    api(libs.edc.dsp)
    api(libs.edc.dataPlaneSignalingApi)
    api(libs.edc.iamMock)

    // data-plane-selector
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.dataPlaneSelectorApi)

    // Test Dependencies
    api(libs.edc.junit)
    api(project(":utils:versions"))
    api(project(":utils:test-utils"))

    // Junit / Mockito / AssertJ
    api(libs.junit.api)
    api(libs.jsonUnit.assertj)
    api(libs.restAssured.restAssured)
    api(libs.mockito.core)
    api(libs.assertj.core)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
