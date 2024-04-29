val edcVersion: String by project
val edcGroup: String by project
val mockitoVersion: String by project
val jupiterVersion: String by project

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

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
