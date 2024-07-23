
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.edc.apiCore)
    implementation(libs.edc.coreSpi)
    implementation(libs.edc.dspApiConfiguration)
    implementation(libs.edc.runtimeMetamodel)
    implementation(libs.okhttp.okhttp)


    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.assertj.core)
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
