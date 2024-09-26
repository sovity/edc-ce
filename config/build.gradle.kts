
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.edc.bootSpi)


    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junitJupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityEdcGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
