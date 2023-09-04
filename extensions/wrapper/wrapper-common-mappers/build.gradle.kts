val lombokVersion: String by project

val edcGroup: String by project
val edcVersion: String by project
val assertj: String by project
val mockitoVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    api("${edcGroup}:policy-model:${edcVersion}")
    api(project(":extensions:wrapper:wrapper-common-api"))

    implementation("org.apache.commons:commons-lang3:3.13.0")
    implementation("org.apache.commons:commons-collections4:4.4")

    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.mockito:mockito-inline:${mockitoVersion}")
    testImplementation("org.mockito:mockito-junit-jupiter:${mockitoVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
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
