val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven(url = "https://maven.repository.redhat.com/ga/")
}

dependencies {
    // Our Wrapper API
    api(project(":extensions:wrapper:wrapper-api"))

    // Required for REST Client Generation
    implementation(platform("org.jboss.resteasy.microprofile:resteasy-microprofile-bom:2.1.0.Final"))
    implementation("org.jboss.resteasy.microprofile:microprofile-rest-client")
    implementation("org.jboss.resteasy.microprofile:microprofile-config")
    implementation("org.jboss.resteasy:resteasy-client:6.2.0.Final")
    implementation("org.jboss.logging:commons-logging-jboss-logging:1.0.0.Final")
    implementation("org.jboss.resteasy:resteasy-jackson2-provider:6.2.0.Final")
    implementation("org.apache.geronimo.config:geronimo-config-impl:1.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}")
    testImplementation(project(":extensions:wrapper:wrapper"))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
