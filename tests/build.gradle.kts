plugins {
    `java-library`
    id("org.gradle.test-retry") version "1.5.7"
}

val assertj: String by project
val edcVersion: String by project
val edcGroup: String by project
val jsonUnit: String by project
val lombokVersion: String by project
val mockitoVersion: String by project

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:auth-mock"))

    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testImplementation(project(":extensions:test-backend-controller"))
    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:${jsonUnit}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.withType<Test> {
    maxParallelForks = 1
    retry {
        maxRetries.set(2)
        maxFailures.set(4)
        failOnPassedAfterRetry.set(false)
    }
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
