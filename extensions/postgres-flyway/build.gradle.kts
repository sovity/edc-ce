
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":config"))

    implementation(libs.edc.coreSpi)
    implementation(libs.edc.sqlCore)

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation(libs.edc.controlPlaneSql)
    implementation(libs.edc.transactionLocal)

    implementation(libs.tractus.sqlPool)

    implementation(libs.apache.commonsLang)

    implementation(libs.flyway.core)

    implementation(libs.postgres)

    implementation(libs.hikari)

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
