
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.flyway.core)
    api(libs.postgres)
    api(libs.hikari)

    implementation(libs.apache.commonsLang)

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
