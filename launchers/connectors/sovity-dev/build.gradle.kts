plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val edcVersion: String by project
val edcGroup: String by project
val mockitoVersion: String by project
val assertj: String by project

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:base-dev"))

    testImplementation(project(":extensions:test-backend-controller"))
    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":extensions:wrapper:client"))
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.getByName<Test>("test") {
    maxParallelForks = 1
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
