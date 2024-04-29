plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // Mock IAM
    api(libs.edc.iamMock)
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
