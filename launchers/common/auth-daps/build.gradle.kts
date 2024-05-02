plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // OAuth2 IAM
    api(libs.edc.oauth2Core)
    api(libs.edc.vaultFilesystem)
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
