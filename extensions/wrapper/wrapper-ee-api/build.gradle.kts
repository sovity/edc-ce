plugins {
    `java-library`
    `maven-publish`
}

dependencies {

    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")

    api("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    api("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.9")
    implementation("io.swagger.core.v3:swagger-jaxrs2-jakarta:2.2.9")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
