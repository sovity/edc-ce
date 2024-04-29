val lombokVersion: String by project

val edcGroup: String by project
val edcVersion: String by project
val assertj: String by project
val mockitoVersion: String by project
val jakartaJsonVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.jakarta.json)
    api(libs.apicatalog.titaniumJsonLd)

    implementation(libs.apache.commonsLang)
    implementation(libs.apache.commonsCollections)
    implementation(libs.apache.commonsIo)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(libs.mockito.mockitoCore)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.junitJupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiterApi)
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
