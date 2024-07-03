
plugins {
    `java-library`
}

dependencies {
    api(libs.junit.api)

    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.jooq.jooq)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
