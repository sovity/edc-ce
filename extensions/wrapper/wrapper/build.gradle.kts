val assertj: String by project
val edcVersion: String by project
val edcGroup: String by project
val jettyGroup: String by project
val jettyVersion: String by project
val jsonUnit: String by project
val lombokVersion: String by project
val mockitoVersion: String by project
val restAssured: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    implementation("${edcGroup}:api-core:${edcVersion}")
    implementation("${edcGroup}:management-api-configuration:${edcVersion}")
    implementation("${edcGroup}:dsp-http-spi:${edcVersion}")
    api(project(":extensions:wrapper:wrapper-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))
    api("${edcGroup}:contract-definition-api:${edcVersion}")
    api("${edcGroup}:control-plane-spi:${edcVersion}")
    api("${edcGroup}:core-spi:${edcVersion}")
    api("${edcGroup}:policy-definition-api:${edcVersion}")
    api("${edcGroup}:transfer-process-api:${edcVersion}")
    implementation("org.apache.commons:commons-lang3:3.13.0")

    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")

    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:policy-always-true"))
    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:dsp:${edcVersion}")
    testImplementation("${edcGroup}:iam-mock:${edcVersion}")
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

    testImplementation("${edcGroup}:json-ld:${edcVersion}")
    testImplementation("${edcGroup}:dsp-http-spi:${edcVersion}")
    testImplementation("${edcGroup}:dsp-api-configuration:${edcVersion}")
    testImplementation("${edcGroup}:data-plane-selector-core:${edcVersion}")

    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:${jsonUnit}")
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.withType<Test> {
    maxParallelForks = 1
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
