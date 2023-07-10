plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project
val mockitoVersion: String by project
val assertj: String by project
val okHttpVersion: String by project
val restAssured: String by project
val testcontainersVersion: String by project
val sovityEdcGroup: String by project
val sovityEdcExtensionsVersion: String by project

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    compileOnly("org.projectlombok:lombok:1.18.28")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("${edcGroup}:control-plane-spi:${edcVersion}")
    implementation("${edcGroup}:management-api-configuration:${edcVersion}")

    api(project(":extensions:broker-server-postgres-flyway-jooq"))
    implementation(project(":extensions:broker-server-api:api"))

    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")
    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.mockito:mockito-inline:${mockitoVersion}")
    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}")
    testImplementation("${edcGroup}:iam-mock:${edcVersion}")
    testImplementation("${edcGroup}:ids:${edcVersion}")
    testImplementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")
    testImplementation("${edcGroup}:configuration-filesystem:${edcVersion}")
    testImplementation(project(":extensions:broker-server-api:client"))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testcontainersVersion}")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    implementation("org.quartz-scheduler:quartz:2.3.2")
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
