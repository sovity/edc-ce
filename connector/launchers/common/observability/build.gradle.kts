plugins {
    `java-library`
}

dependencies {
    // Logging
    api(libs.edc.monitorJdkLogger)
}

group = libs.versions.sovityEdcGroup.get()
