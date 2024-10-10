
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.edc.coreSpi)
    api(libs.edc.controlPlaneSpi)
    implementation(libs.edc.apiCore)
    implementation(libs.edc.managementApiConfiguration)

    implementation(libs.jakarta.rsApi)
    implementation(libs.jakarta.validationApi)

    testImplementation(project(":launchers:utils:edc-integration-test"))
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
