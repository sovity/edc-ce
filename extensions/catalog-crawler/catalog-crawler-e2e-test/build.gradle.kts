plugins {
    `java-library`
}

dependencies {
    compileOnly(project(":launchers:connectors:catalog-crawler-dev"))
    compileOnly(project(":launchers:connectors:sovity-dev"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":utils:versions"))
    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":utils:json-and-jsonld-utils"))
    testImplementation(project(":extensions:catalog-crawler:catalog-crawler-db"))
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:catalog-crawler:catalog-crawler"))

    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.edc.junit)
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
