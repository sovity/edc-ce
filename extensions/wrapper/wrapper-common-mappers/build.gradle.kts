val lombokVersion: String by project

val assertj: String by project
val edcGroup: String by project
val edcVersion: String by project
val jsonUnit: String by project
val mockitoVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.edc.policyModel)
    api(libs.edc.coreSpi)
    api(libs.edc.transformCore)
    api(libs.edc.transformSpi)
    api(project(":extensions:wrapper:wrapper-common-api"))
    api(project(":utils:json-and-jsonld-utils"))
    implementation(libs.apache.commonsLang)
    implementation(libs.apache.commonsCollections)
    implementation(libs.flexmark.all)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.jsonUnit)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiterApi)
    testImplementation(libs.mockito.mockitoCore)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.junitJupiter)
    testRuntimeOnly(libs.junit.jupiterEngine)
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
