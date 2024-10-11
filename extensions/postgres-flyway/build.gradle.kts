
plugins {
    `java-library`
    `maven-publish`
}

var edcVersion = libs.versions.edc.get()

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":config"))

    // implementation(libs.edc.coreSpi)
    implementation(libs.edc.sqlCore)

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation(libs.edc.controlPlaneSql)
    implementation(libs.edc.transactionLocal)

    implementation(libs.apache.commonsLang)
    implementation(libs.flyway.core)
    implementation(libs.postgres)
    implementation(libs.hikari)

    implementation("org.eclipse.edc:sql-pool-apache-commons:${edcVersion}")

    implementation("org.eclipse.edc:data-plane-instance-store-sql:${edcVersion}")
    implementation("org.eclipse.edc:edr-index-sql:${edcVersion}")
    implementation("org.eclipse.edc:policy-monitor-store-sql:${edcVersion}")

    testImplementation(libs.edc.junit)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
