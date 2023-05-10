plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val edcVersion: String by project
val edcGroup: String by project

val sovityEdcExtensionsGroup: String by project
val sovityEdcExtensionsVersion: String by project

dependencies {
    // Control-Plane
    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:management-api:${edcVersion}")
    implementation("${edcGroup}:api-observability:${edcVersion}")
    implementation("${edcGroup}:configuration-filesystem:${edcVersion}")
    implementation("${edcGroup}:control-plane-aggregate-services:${edcVersion}")
    implementation("${edcGroup}:http:${edcVersion}")
    implementation("${edcGroup}:ids:${edcVersion}")

    // JDK Logger
    implementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")

    // Optional: PostgreSQL + Flyway
    if (project.hasProperty("postgres-flyway")) {
        implementation("${sovityEdcExtensionsGroup}:postgres-flyway:${sovityEdcExtensionsVersion}")
    }

    // Optional: Connector-To-Connector IAM
    if (project.hasProperty("oauth2")) {
        implementation("${edcGroup}:oauth2-core:${edcVersion}")
    } else {
        implementation("${edcGroup}:iam-mock:${edcVersion}")
    }
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}
