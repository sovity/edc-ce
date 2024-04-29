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
    api("com.apicatalog:titanium-json-ld:1.3.2")

    implementation(libs.apache.commonsLang)
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-io:commons-io:2.13.0")

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
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
