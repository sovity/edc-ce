plugins {
    `java-library`
}

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:auth-mock"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:test-backend-controller"))
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":utils:test-utils"))
    testImplementation(project(":utils:jooq-database-access"))

    testImplementation(libs.assertj.core)
    testImplementation(libs.jooq.jooq)
    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockserver.netty)
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.restAssured.restAssured)

    testRuntimeOnly(libs.junit.engine)
}

tasks.getByName<Test>("test") {
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}

group = libs.versions.sovityEdcGroup.get()
