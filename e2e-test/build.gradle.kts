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
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")

    testImplementation(project(":launcher:connector"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.apache.commons:commons-lang3:3.13.0")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:sql-core:${edcVersion}")
    testImplementation("${edcGroup}:json-ld-spi:${edcVersion}")
    testImplementation("${edcGroup}:json-ld:${edcVersion}")
    testImplementation("${edcGroup}:transfer-spi:${edcVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("${edcGroup}:contract-spi:${edcVersion}")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.awaitility:awaitility:${awaitilityVersion}")

}