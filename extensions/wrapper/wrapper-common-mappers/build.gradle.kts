val lombokVersion: String by project

val edcGroup: String by project
val edcVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    api("${edcGroup}:policy-definition-api:${edcVersion}")
    api(project(":extensions:wrapper:wrapper-common-api"))

    implementation("org.apache.commons:commons-lang3:3.13.0")
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
