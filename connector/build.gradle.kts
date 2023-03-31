plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // Control-Plane
    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:management-api:${edcVersion}")
    implementation("${edcGroup}:api-observability:${edcVersion}")
    implementation("${edcGroup}:configuration-filesystem:${edcVersion}")
    implementation("${edcGroup}:control-plane-aggregate-services:${edcVersion}")
    implementation("${edcGroup}:http:${edcVersion}")
    implementation("${edcGroup}:ids:${edcVersion}")

    // Control-plane to Data-plane
    implementation("${edcGroup}:transfer-data-plane:${edcVersion}")
    implementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    implementation("${edcGroup}:data-plane-selector-client:${edcVersion}")

    // Data-plane
    implementation("${edcGroup}:data-plane-http:${edcVersion}")
    implementation("${edcGroup}:data-plane-framework:${edcVersion}")
    implementation("${edcGroup}:data-plane-core:${edcVersion}")
    implementation("${edcGroup}:data-plane-util:${edcVersion}")

    // JDK Logger
    implementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")

    // sovity Extensions Package
    implementation(project(":extensions:sovity-edc-extensions-package"))

    // Optional: PostgreSQL + Flyway
    if (project.hasProperty("postgres-flyway")) {
        implementation(project(":extensions:postgres-flyway"))
    }

    // Optional: MDS Extensions
    if (project.hasProperty("mds")) {
        implementation(project(":extensions:ids-clearinghouse-client"))
        implementation(project(":extensions:ids-broker-client"))
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
