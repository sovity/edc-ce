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

    api("${edcGroup}:policy-model:${edcVersion}")
    api(libs.edc.coreSpi)
    api("${edcGroup}:transform-core:${edcVersion}")
    api("${edcGroup}:transform-spi:${edcVersion}")
    api(project(":extensions:wrapper:wrapper-common-api"))
    api(project(":utils:json-and-jsonld-utils"))
    implementation(libs.apache.commonsLang)
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation("${edcGroup}:json-ld:${edcVersion}")
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:${jsonUnit}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation(libs.junit.jupiterApi)
    testImplementation(libs.mockito.mockitoCore)
    testImplementation("org.mockito:mockito-inline:${mockitoVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
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
