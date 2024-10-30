plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)

    compileOnly(libs.lombok)

    implementation(project(":utils:json-and-jsonld-utils"))
    implementation(project(":config"))

    implementation(libs.edc.controlPlaneCore)
    implementation(libs.edc.dspApiConfiguration)
    implementation(libs.edc.dspHttpSpi)
    implementation(libs.edc.httpSpi)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.transformLib)

    implementation(libs.jackson.jsr310)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":launchers:utils:vanilla-control-plane"))
    testImplementation(libs.edc.jsonLdLib)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
