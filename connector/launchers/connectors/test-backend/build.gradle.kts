
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
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}

group = libs.versions.sovityEdcExtensionGroup.get()
