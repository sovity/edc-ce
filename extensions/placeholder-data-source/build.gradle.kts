
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.edc.coreSpi)
    api(libs.edc.controlPlaneSpi)
    implementation(libs.edc.apiCore)
    implementation(libs.edc.dspApiConfiguration)
    implementation(libs.okhttp.okhttp)

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
