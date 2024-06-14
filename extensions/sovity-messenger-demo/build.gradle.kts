plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
}

dependencies {
    annotationProcessor(libs.lombok)

    compileOnly(libs.lombok)

    implementation(project(":extensions:sovity-messenger"))

    implementation(libs.edc.controlPlaneCore)
    implementation(libs.edc.dspApiConfiguration)
    implementation(libs.edc.dspHttpSpi)
    implementation(libs.edc.httpSpi)
    implementation(libs.edc.managementApiConfiguration)

//    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
//    implementation("org.json:json:${jsonVersion}")
//    implementation("org.glassfish.jersey.media:jersey-media-multipart:3.1.3")
//
//    implementation("de.fraunhofer.iais.eis.ids.infomodel:infomodel-java:1.0.2-basecamp")
//    implementation("de.fraunhofer.iais.eis.ids.infomodel:infomodel-util:1.0.2-basecamp")
//

    testAnnotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)

    testImplementation(project(":utils:test-connector-remote"))

    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.edc.dspApiConfiguration)
    testImplementation(libs.edc.dspHttpCore)
    testImplementation(libs.edc.iamMock)
    testImplementation(libs.edc.jsonLd)

    testImplementation(libs.edc.http) {
        exclude(group = "org.eclipse.jetty", module = "jetty-client")
        exclude(group = "org.eclipse.jetty", module = "jetty-http")
        exclude(group = "org.eclipse.jetty", module = "jetty-io")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
        exclude(group = "org.eclipse.jetty", module = "jetty-util")
        exclude(group = "org.eclipse.jetty", module = "jetty-webapp")
    }

    // Updated jetty versions for e.g. CVE-2023-26048
    testImplementation(libs.bundles.jetty.cve2023)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.jsonAssert)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)

    testRuntimeOnly(libs.junit.engine)
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
