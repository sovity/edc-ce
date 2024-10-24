plugins {
    `java-library`
}

dependencies {
    // OAuth2 IAM
    api(libs.edc.oauth2Core)
}

group = libs.versions.sovityEdcGroup.get()
