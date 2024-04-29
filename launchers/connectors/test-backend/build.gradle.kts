val edcVersion: String by project
val edcGroup: String by project

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    api("${edcGroup}:connector-core:${edcVersion}")
    api(libs.edc.boot)
    api(libs.edc.http)
    api(libs.edc.apiObservability)
    api(project(":extensions:test-backend-controller"))
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup
