plugins {
    `java-library`
    id("org.gradle.test-retry") version "1.5.7"
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
    testImplementation(libs.jsonUnit)
    testImplementation(libs.mockito.mockitoCore)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiterApi)
    testImplementation(libs.junit.jupiterParams)
    testImplementation(libs.mockserver.netty)
    testRuntimeOnly(libs.junit.jupiterEngine)
}

tasks.withType<Test> {
    maxParallelForks = 1
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
