
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":config"))
    implementation(project(":utils:jooq-database-access"))
    implementation(project(":extensions:database-direct-access"))
    implementation(project(":extensions:sovity-messenger"))

    implementation(libs.edc.coreSpi)
    implementation(libs.edc.dspNegotiationTransform)
    implementation(libs.edc.transferSpi)

    implementation(libs.jakarta.rsApi)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":launchers:utils:vanilla-control-plane"))
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
