plugins {
    `java-library`
}

dependencies {
    implementation(libs.loggingHouse.client)
}

group = libs.versions.sovityEdcGroup.get()
