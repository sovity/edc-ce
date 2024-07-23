plugins {
    `java-library`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.edc.controlPlaneSpi)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.runtimeMetamodel)

    implementation(libs.quartz.quartz)
    implementation(libs.apache.commonsLang)
    implementation(project(":utils:versions"))

    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":extensions:catalog-crawler:catalog-crawler-db"))
    api(project(":extensions:placeholder-data-source"))
    api(project(":extensions:postgres-flyway-core"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
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
    maxParallelForks = 1
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
