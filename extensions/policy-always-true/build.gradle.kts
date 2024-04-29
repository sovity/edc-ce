val edcVersion: String by project
val edcGroup: String by project
val mockitoVersion: String by project

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
    testImplementation(libs.mockito.mockitoCore)
    testImplementation(libs.junit.jupiterApi)
    testRuntimeOnly(libs.junit.jupiterEngine)
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
