val lombokVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    api("jakarta.validation:jakarta.validation-api:3.0.2")
    api("io.swagger.core.v3:swagger-annotations-jakarta:2.2.15")
    api("io.swagger.core.v3:swagger-jaxrs2-jakarta:2.2.15")
    api("jakarta.servlet:jakarta.servlet-api:5.0.0")

    implementation(libs.apache.commons.lang)
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
