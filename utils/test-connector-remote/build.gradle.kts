
plugins {
    `java-library`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.junit.api)
    implementation(libs.apache.commonsLang)

    api(libs.edc.junit)
    api(libs.awaitility.java)
    api(project(":utils:json-and-jsonld-utils"))
    implementation(project(":utils:versions"))
    implementation(libs.edc.sqlCore)
    implementation(libs.edc.jsonLdSpi)
    implementation(libs.edc.jsonLd)
    implementation(libs.assertj.core)
    implementation(libs.testcontainers.testcontainers)
    implementation(libs.testcontainers.junitJupiter)
    implementation(libs.testcontainers.postgresql)
    implementation(libs.restAssured.restAssured)
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
