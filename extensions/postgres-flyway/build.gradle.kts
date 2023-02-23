val edcVersion: String by project
val edcGroup: String by project
val flywayVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation("${edcGroup}:core-spi:${edcVersion}")
    implementation("${edcGroup}:sql-core:${edcVersion}")

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