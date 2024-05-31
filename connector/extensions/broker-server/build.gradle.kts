plugins {
    `java-library`
    alias(libs.plugins.retry)
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.apache.commonsLang)

    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))

    implementation(libs.edc.controlPlaneSpi)
    implementation(libs.edc.managementApiConfiguration)

    api(project(":extensions:broker-server-postgres-flyway-jooq"))
    implementation(project(":extensions:broker-server-api:api"))
    implementation(project(":utils:versions"))

    implementation(libs.okhttp.okhttp)

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
    testImplementation(project(":extensions:broker-server-api:client"))
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.junit.api)
    testImplementation(libs.jsonAssert)
    testRuntimeOnly(libs.junit.engine)

    implementation(libs.quartz.quartz)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    maxParallelForks = 1
    retry {
        maxRetries.set(2)
        maxFailures.set(4)
        failOnPassedAfterRetry.set(false)
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}

group = libs.versions.sovityBrokerServerGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
