plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:auth-mock"))
    api(project(":launchers:common:observability"))
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
