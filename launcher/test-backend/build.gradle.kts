val edcVersion: String by project
val edcGroup: String by project

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation("${edcGroup}:connector-core:${edcVersion}")
    implementation("${edcGroup}:boot:${edcVersion}")
    implementation("${edcGroup}:http:${edcVersion}")
    implementation(project(":extensions:test-backend-controller"))
}

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}
