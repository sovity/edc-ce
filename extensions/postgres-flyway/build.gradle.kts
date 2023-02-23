val edcVersion: String by project
val edcGroup: String by project
val flywayVersion: String by project
val postgresVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation("${edcGroup}:core-spi:${edcVersion}")
    implementation("${edcGroup}:sql-core:${edcVersion}")

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation("${edcGroup}:control-plane-sql:${edcVersion}")
    implementation("${edcGroup}:data-plane-instance-store-sql:${edcVersion}")
    implementation("${edcGroup}:sql-pool-apache-commons:${edcVersion}")
    implementation("${edcGroup}:transaction-local:${edcVersion}")
    implementation("org.postgresql:postgresql:${postgresVersion}")

    implementation("org.flywaydb:flyway-core:${flywayVersion}")

    testImplementation("${edcGroup}:junit:${edcVersion}")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}