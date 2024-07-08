plugins {
    `java-library`
}

dependencies {
    api(project(":launchers:connectors:sovity-dev"))
    api(project(":extensions:test-backend-controller"))
}

group = libs.versions.sovityEdcGroup.get()
