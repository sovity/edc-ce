plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project
val mockitoVersion: String by project
val assertj: String by project
val okHttpVersion: String by project
val sovityEdcGroup: String by project
val sovityEdcExtensionsVersion: String by project
val restAssured: String by project
val testcontainersVersion: String by project

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    compileOnly("org.projectlombok:lombok:1.18.28")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:management-api-configuration:${edcVersion}")
    implementation("${edcGroup}:ids-spi:${edcVersion}")
    implementation("${edcGroup}:ids-api-multipart-dispatcher-v1:${edcVersion}")
    implementation("${edcGroup}:ids-api-configuration:${edcVersion}")
    implementation("${edcGroup}:ids-jsonld-serdes:${edcVersion}")

    api(project(":extensions:broker-server-postgres-flyway-jooq"))
    api("${sovityEdcGroup}:wrapper-broker-api:${sovityEdcExtensionsVersion}")

    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}")
    testImplementation("${edcGroup}:iam-mock:${edcVersion}")
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("${sovityEdcGroup}:client:${sovityEdcExtensionsVersion}")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel") {}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
