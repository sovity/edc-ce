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

    implementation(project(":extensions:test-backend-controller"))
    implementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("${edcGroup}:junit:${edcVersion}")
    implementation("${edcGroup}:sql-core:${edcVersion}")
    implementation("${edcGroup}:json-ld-spi:${edcVersion}")
    implementation("${edcGroup}:json-ld:${edcVersion}")
    implementation("${edcGroup}:transfer-spi:${edcVersion}")
    implementation("org.assertj:assertj-core:${assertj}")
    implementation("${edcGroup}:contract-spi:${edcVersion}")
    implementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    implementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    implementation("org.testcontainers:postgresql:${testcontainersVersion}")
    implementation("io.rest-assured:rest-assured:${restAssured}")
    implementation("org.awaitility:awaitility:${awaitilityVersion}")

}