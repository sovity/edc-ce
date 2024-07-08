plugins {
    `java-library`
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.edc.controlPlaneSpi)
    implementation(libs.edc.managementApiConfiguration)

    implementation(libs.quartz.quartz)
    implementation(libs.apache.commonsLang)
    implementation(libs.hikari)
    implementation(project(":utils:versions"))

    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":extensions:catalog-crawler:catalog-crawler-db"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:sovity-edc-extensions-package"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.http)
    testImplementation(libs.edc.iamMock)
    testImplementation(libs.edc.dsp)
    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.edc.monitorJdkLogger)
    testImplementation(libs.edc.configurationFilesystem)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.flyway.core)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.junit.api)
    testImplementation(libs.jsonAssert)
    testRuntimeOnly(libs.junit.engine)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
