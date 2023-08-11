plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {

    implementation(project(":launcher:connector-base"))

    // Optional: PostgreSQL + Flyway
    if (project.hasProperty("postgres-flyway")) {
        implementation(project(":extensions:postgres-flyway"))
    }

    // Optional: Connector-To-Connector IAM
    if (project.hasProperty("oauth2")) {
        implementation("${edcGroup}:oauth2-core:${edcVersion}")
    } else {
        implementation("${edcGroup}:iam-mock:${edcVersion}")
    }

    // Optional: Data Management API IAM
    if (project.hasProperty("dmgmt-api-key")) {
        implementation("${edcGroup}:auth-tokenbased:${edcVersion}")
    }

    // Optional: Fs Vault
    if (project.hasProperty("fs-vault")) {
        implementation("${edcGroup}:vault-filesystem:${edcVersion}")
    }

    // Optional: Azure Vault
    if (project.hasProperty("azure-vault")) {
        implementation("${edcGroup}:azure-vault:${edcVersion}")
    }
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
