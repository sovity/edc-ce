plugins {
    `java-library`
}

dependencies {
    api(libs.edc.apiCore)
    api(libs.edc.coreSpi)
    api(libs.edc.http)
    implementation(libs.edc.runtimeMetamodel)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
