
plugins {
    `java-library`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.apache.commonsLang)
    api(libs.edc.contractSpi)
    api(libs.edc.jsonLdSpi)
    api(libs.edc.jsonLdLib)

    api(libs.edc.junit)
    api(libs.awaitility.java)
    api(libs.postgres)
    api(project(":config"))
    api(project(":extensions:wrapper:clients:java-client"))
    api(project(":utils:json-and-jsonld-utils"))


    api(libs.junit.api)

    implementation(project(":utils:versions"))
    implementation(libs.edc.jsonLd)
    implementation(libs.assertj.core)
    implementation(libs.mockito.core)
    implementation(libs.jooq.jooq)
    implementation(libs.mockserver.netty)
    implementation(libs.testcontainers.testcontainers)
    implementation(libs.testcontainers.junitJupiter)
    implementation(libs.testcontainers.postgresql)
    implementation(libs.restAssured.restAssured)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
