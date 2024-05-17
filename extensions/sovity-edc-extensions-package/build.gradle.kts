
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    // Policies
    api(project(":extensions:policy-referring-connector"))
    api(project(":extensions:policy-time-interval"))
    api(project(":extensions:policy-always-true"))

    // API Extensions
    api(project(":extensions:edc-ui-config"))
    api(project(":extensions:last-commit-info"))
    api(project(":extensions:wrapper:wrapper"))
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
