plugins {
    `java-library`
}

dependencies {
    // Mock IAM
    api(libs.edc.iamMock)
}

group = libs.versions.sovityEdcGroup.get()
