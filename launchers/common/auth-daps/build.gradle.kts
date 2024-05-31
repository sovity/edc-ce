plugins {
    `java-library`
}

dependencies {
    // OAuth2 IAM
    api(libs.edc.oauth2Core)
    api(libs.edc.vaultFilesystem)
}

group = libs.versions.sovityEdcGroup.get()
