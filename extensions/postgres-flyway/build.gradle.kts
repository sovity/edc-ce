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
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.edc.coreSpi)
    implementation(libs.edc.sqlCore)

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation(libs.edc.controlPlaneSql)
    implementation(libs.edc.transactionLocal)
    implementation(libs.tractus.sqlPool)

    implementation(libs.postgres)

    implementation(libs.flyway.core)

    testImplementation(libs.edc.junit)
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
