
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.edc.coreSpi)
    api(libs.edc.policyEngineSpi)
    api(libs.edc.controlPlaneSpi)
    implementation(libs.edc.apiCore)

    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
