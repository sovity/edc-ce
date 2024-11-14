
plugins {
    `java-library`
    `maven-publish`
}

var edcVersion = libs.versions.edc.get()

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":config"))

    implementation(libs.edc.sqlCore)
    implementation(libs.edc.httpSpi)

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation(libs.edc.controlPlaneSql)
    implementation(libs.edc.transactionLocal)

    implementation(libs.apache.commonsLang)
    implementation(libs.flyway.core)
    implementation(libs.postgres)
    implementation(libs.hikari)

    // TODO check which ones are required in the EDC CE
    implementation(libs.edc.sqlPoolApacheCommons)
    implementation(libs.edc.dataPlaneInstanceStoreSql)
    implementation(libs.edc.edrIndexSql)
    implementation(libs.edc.policyMonitorStoreSql)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
