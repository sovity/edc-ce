plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // Control-Plane
    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    implementation("${edcGroup}:api-observability:${edcVersion}")
    implementation("${edcGroup}:configuration-filesystem:${edcVersion}")
    implementation("${edcGroup}:control-plane-aggregate-services:${edcVersion}")
    implementation("${edcGroup}:http:${edcVersion}")
    implementation("${edcGroup}:dsp:${edcVersion}")
    implementation("${edcGroup}:json-ld:${edcVersion}")

    // JDK Logger
    implementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")

    // Broker Server + PostgreSQL + Flyway
    implementation(project(":extensions:broker-server"))

    // Connector-To-Connector IAM
    implementation("${edcGroup}:iam-mock:${edcVersion}")
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}
