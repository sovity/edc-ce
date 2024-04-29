plugins {
    `java-library`
    alias(libs.plugins.retry)
}

val assertj: String by project
val edcVersion: String by project
val edcGroup: String by project
val httpMockServerVersion: String by project
val jsonUnit: String by project
val jupiterVersion: String by project
val lombokVersion: String by project
val mockitoVersion: String by project

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:auth-mock"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation(project(":extensions:test-backend-controller"))
    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockserver.netty)
    testRuntimeOnly(libs.junit.engine)
}

tasks.withType<Test> {
    maxParallelForks = 1
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
