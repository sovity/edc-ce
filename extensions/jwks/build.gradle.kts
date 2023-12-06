val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project
val mockitoVersion: String by project
val lombokVersion: String by project
val jettyVersion: String by project
val jettyGroup: String by project
val nimbusJoseJwtVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation("com.nimbusds:nimbus-jose-jwt:${nimbusJoseJwtVersion}")
    implementation("${edcGroup}:dsp-api-configuration:${edcVersion}")

    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    api("${edcGroup}:core-spi:${edcVersion}")
    api("${edcGroup}:control-plane-spi:${edcVersion}")
    implementation("${edcGroup}:api-core:${edcVersion}")

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")

    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}") {
        exclude(group = "org.eclipse.jetty", module = "jetty-client")
        exclude(group = "org.eclipse.jetty", module = "jetty-http")
        exclude(group = "org.eclipse.jetty", module = "jetty-io")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
        exclude(group = "org.eclipse.jetty", module = "jetty-util")
        exclude(group = "org.eclipse.jetty", module = "jetty-webapp")
    }

    // Updated jetty versions for e.g. CVE-2023-26048
    testImplementation("${jettyGroup}:jetty-client:${jettyVersion}")
    testImplementation("${jettyGroup}:jetty-http:${jettyVersion}")
    testImplementation("${jettyGroup}:jetty-io:${jettyVersion}")
    testImplementation("${jettyGroup}:jetty-server:${jettyVersion}")
    testImplementation("${jettyGroup}:jetty-util:${jettyVersion}")
    testImplementation("${jettyGroup}:jetty-webapp:${jettyVersion}")

    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
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
