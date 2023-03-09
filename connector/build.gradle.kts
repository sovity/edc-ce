plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {

    if (project.hasProperty("dev-edc")) {
        implementation("${edcGroup}:control-plane-core:${edcVersion}")
        implementation("${edcGroup}:api-observability:${edcVersion}")
        implementation("${edcGroup}:iam-mock:${edcVersion}")
        implementation("${edcGroup}:auth-tokenbased:${edcVersion}")
        implementation("${edcGroup}:management-api:${edcVersion}")
        implementation("${edcGroup}:ids:${edcVersion}")
        implementation(project(":extensions:policy-always-true"))
        implementation(project(":extensions:contract-agreement-transfer-api"))
        implementation(project(":extensions:last-commit-info"))
        implementation(project(":extensions:edc-ui-config"))
    } else {
        // Control-plane
        implementation(project(":extensions:broker"))
        implementation(project(":extensions:clearinghouse"))
        implementation(project(":extensions:policy-referring-connector"))
        implementation(project(":extensions:policy-time-interval"))
        implementation(project(":extensions:contract-agreement-transfer-api"))
        implementation(project(":extensions:policy-always-true"))
        implementation(project(":extensions:last-commit-info"))
        implementation(project(":extensions:edc-ui-config"))
        implementation(project(":extensions:postgres-flyway"))
        implementation("${edcGroup}:control-plane-core:${edcVersion}")
        implementation("${edcGroup}:api-observability:${edcVersion}")
        implementation("${edcGroup}:management-api:${edcVersion}")
        implementation("${edcGroup}:configuration-filesystem:${edcVersion}")
        implementation("${edcGroup}:http:${edcVersion}")
        implementation("${edcGroup}:control-plane-aggregate-services:${edcVersion}")

        // Control-plane to Data-plane
        implementation("${edcGroup}:transfer-data-plane:${edcVersion}")
        implementation("${edcGroup}:data-plane-selector-client:${edcVersion}")

        // Data-plane
        implementation("${edcGroup}:data-plane-http:${edcVersion}")
        implementation("${edcGroup}:data-plane-framework:${edcVersion}")
        implementation("${edcGroup}:data-plane-core:${edcVersion}")
        implementation("${edcGroup}:data-plane-util:${edcVersion}")

        // JDK Logger
        implementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")

        // IDS
        implementation("${edcGroup}:ids:${edcVersion}")

        // To use FileSystem vault e.g. -DuseFsVault="true".Only for non-production usages.
        val useFsVault: Boolean = System.getProperty("useFsVault", "true").toBoolean()
        if (useFsVault) {
            implementation("${edcGroup}:vault-filesystem:${edcVersion}")
        } else {
            implementation("${edcGroup}:azure-vault:${edcVersion}")
        }

        // Authentication for IDS API
        implementation("${edcGroup}:oauth2-core:${edcVersion}")
        implementation("${edcGroup}:jwt-core:${edcVersion}")
    }
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}