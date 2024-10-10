
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.edc.coreSpi)
    api(libs.edc.policyEngineSpi)
    api(libs.edc.controlPlaneSpi)
    implementation(libs.edc.apiCore)

    testImplementation(project(":launchers:utils:vanilla-control-plane"))
    testImplementation(project(":utils:test-utils"))
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
