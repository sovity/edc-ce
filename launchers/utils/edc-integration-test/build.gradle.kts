
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    // EDC Base
    api(libs.edc.controlPlaneCore)
    api(libs.edc.dsp)
    api(libs.edc.iamMock)
    api(libs.edc.http) {
        exclude(group = "org.eclipse.jetty", module = "jetty-client")
        exclude(group = "org.eclipse.jetty", module = "jetty-http")
        exclude(group = "org.eclipse.jetty", module = "jetty-io")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
        exclude(group = "org.eclipse.jetty", module = "jetty-util")
        exclude(group = "org.eclipse.jetty", module = "jetty-webapp")
    }
    // Updated jetty versions for e.g. CVE-2023-26048
    api(libs.bundles.jetty.cve2023)
    api(libs.edc.controlPlaneSql)
    api(libs.edc.contractNegotiationStoreSql)
    api(libs.edc.dspHttpSpi)
    api(libs.edc.dspApiConfiguration)
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.jsonLd)
    api(libs.edc.transferProcessStoreSql)

    // EDC Extensions
    api(project(":extensions:postgres-flyway"))
    api(project(":utils:versions"))

    // Test Dependencies
    api(libs.edc.junit)
    api(project(":utils:test-utils"))

    // Junit / Mockito / AssertJ
    api(libs.junit.api)
    api(libs.jsonUnit.assertj)
    api(libs.restAssured.restAssured)
    api(libs.mockito.core)
    api(libs.assertj.core)

    // Let's hope this works
    runtimeOnly(libs.junit.engine)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
