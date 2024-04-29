val edcVersion: String by project
val edcGroup: String by project
val testcontainersVersion: String by project
val lombokVersion: String by project
val restAssured: String by project
val awaitilityVersion: String by project
val assertj: String by project

plugins {
    `java-library`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.junit.jupiterApi)
    implementation(libs.apache.commonsLang)

    api(libs.edc.junit)
    api(libs.awaitility)
    api(project(":utils:json-and-jsonld-utils"))
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
