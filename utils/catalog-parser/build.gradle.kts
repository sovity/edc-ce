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

    api("org.glassfish:jakarta.json:${jakartaJsonVersion}")
    api(libs.edc.coreSpi)
    api(libs.edc.controlPlaneSpi)
    api("${edcGroup}:json-ld:${edcVersion}")

    implementation(project(":utils:json-and-jsonld-utils"))

    implementation(libs.apache.commons.lang)
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-io:commons-io:2.13.0")

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.mockito.mockitoCore)
    testImplementation("org.mockito:mockito-inline:${mockitoVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
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
