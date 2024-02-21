val edcVersion: String by project
val edcGroup: String by project
val tractusVersion: String by project
val tractusGroup: String by project
val flywayVersion: String by project
val postgresVersion: String by project
val lombokVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    implementation("${edcGroup}:core-spi:${edcVersion}")
    implementation("${edcGroup}:sql-core:${edcVersion}")

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation("${edcGroup}:control-plane-sql:${edcVersion}")
    implementation("${tractusGroup}:sql-pool:${tractusVersion}")
    implementation("${edcGroup}:transaction-local:${edcVersion}")

    implementation("org.postgresql:postgresql:${postgresVersion}")

    implementation("org.flywaydb:flyway-core:${flywayVersion}")

    testImplementation("${edcGroup}:junit:${edcVersion}")
}

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
