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
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.edc.apiCore)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.dspHttpSpi)
    api(project(":extensions:wrapper:wrapper-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))
    api(libs.edc.contractDefinitionApi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.policyDefinitionApi)
    api(libs.edc.transferProcessApi)
    implementation(libs.apache.commons.lang)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:policy-always-true"))
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.dsp)
    testImplementation(libs.edc.iamMock)
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.http) {
        // TODO: group
        exclude(group = "org.eclipse.jetty", module = "jetty-client")
        exclude(group = "org.eclipse.jetty", module = "jetty-http")
        exclude(group = "org.eclipse.jetty", module = "jetty-io")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
        exclude(group = "org.eclipse.jetty", module = "jetty-util")
        exclude(group = "org.eclipse.jetty", module = "jetty-webapp")
    }

    // Updated jetty versions for e.g. CVE-2023-26048
    testImplementation(libs.bundles.jetty.cve2023)

    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.edc.dspHttpSpi)
    testImplementation(libs.edc.dspApiConfiguration)
    testImplementation(libs.edc.dataPlaneSelectorCore)

    testImplementation(libs.jsonUnit)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.mockito.mockitoCore)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiterApi)
    testRuntimeOnly(libs.junit.jupiterEngine)
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
