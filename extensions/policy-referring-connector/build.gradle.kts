
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.edc.authSpi)
    api(libs.edc.policyEngineSpi)
    api(libs.edc.contractSpi)


    testImplementation(libs.edc.junit)

    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
