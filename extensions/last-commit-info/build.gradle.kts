val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project
val mockitoVersion: String by project
val lombokVersion: String by project
val jettyVersion: String by project
val jettyGroup: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    api(libs.edc.coreSpi)
    api(libs.edc.controlPlaneSpi)
    implementation(libs.edc.apiCore)
    implementation(libs.edc.managementApiConfiguration)

    implementation(libs.jakarta.rsApi)
    implementation(libs.jakarta.validationApi)

    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")

    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.http) {
        // TODO: find how to group this with buildSrc
        exclude(group = "org.eclipse.jetty", module = "jetty-client")
        exclude(group = "org.eclipse.jetty", module = "jetty-http")
        exclude(group = "org.eclipse.jetty", module = "jetty-io")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
        exclude(group = "org.eclipse.jetty", module = "jetty-util")
        exclude(group = "org.eclipse.jetty", module = "jetty-webapp")
    }

    // Updated jetty versions for e.g. CVE-2023-26048
    testImplementation(libs.bundles.jetty.cve2023)

    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
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
