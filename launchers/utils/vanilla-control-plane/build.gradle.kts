
plugins {
    `java-library`
    `maven-publish`
}
val edcVersion = libs.versions.edc.get()
val edcGroup = libs.versions.edcGroup.get()

dependencies {
    // EDC Base
    api("${edcGroup}:json-ld:${edcVersion}")
    api("${edcGroup}:connector-core:${edcVersion}")
    api("${edcGroup}:policy-monitor-core:${edcVersion}")

    // EDC CP
    api("${edcGroup}:control-plane-core:${edcVersion}")
    api("${edcGroup}:management-api:${edcVersion}")
    api("${edcGroup}:control-api-configuration:${edcVersion}")
    api("${edcGroup}:control-plane-api:${edcVersion}")
    api("${edcGroup}:http:${edcVersion}")
    api("${edcGroup}:dsp:${edcVersion}")
    api(libs.edc.iamMock)

    // data-plane-selector
    api("${edcGroup}:data-plane-selector-core:${edcVersion}")
    api("${edcGroup}:data-plane-selector-api:${edcVersion}")

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
