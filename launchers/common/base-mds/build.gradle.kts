plugins {
    `java-library`
}

dependencies {
    implementation(libs.loggingHouse.client)
    implementation(project(":extensions:mds-logginghouse-binder"))
}

group = libs.versions.sovityEdcGroup.get()
