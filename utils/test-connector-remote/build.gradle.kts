val edcVersion: String by project
val edcGroup: String by project
val testcontainersVersion: String by project
val lombokVersion: String by project
val restAssured: String by project
val awaitilityVersion: String by project
val assertj: String by project

plugins {
    `java-library`
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    api("org.junit.jupiter:junit-jupiter-api:5.10.0")
    implementation("org.apache.commons:commons-lang3:3.14.0")

    api("${edcGroup}:junit:${edcVersion}")
    api("org.awaitility:awaitility:${awaitilityVersion}")
    api(project(":utils:json-and-jsonld-utils"))
    implementation("${edcGroup}:sql-core:${edcVersion}")
    implementation("${edcGroup}:json-ld-spi:${edcVersion}")
    implementation("${edcGroup}:json-ld:${edcVersion}")
    implementation("org.assertj:assertj-core:${assertj}")
    implementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    implementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    implementation("org.testcontainers:postgresql:${testcontainersVersion}")
    implementation("io.rest-assured:rest-assured:${restAssured}")
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
