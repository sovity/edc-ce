
plugins {
    `java-library`
}

dependencies {
    api(libs.junit.api)

    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
