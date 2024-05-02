plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    api(project(":launchers:common:base"))
    api(project(":launchers:common:base-mds"))
    api(project(":launchers:common:auth-daps"))
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
