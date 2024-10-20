
plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

dependencies {
    api(libs.edc.connectorCore)
    api(libs.edc.boot)
    api(libs.edc.http)
    api(libs.edc.apiObservability)
    api(project(":extensions:test-backend-controller"))
}

application {
    mainClass.set("de.sovity.edc.utils.config.CeMain")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}

group = libs.versions.sovityEdcExtensionGroup.get()
