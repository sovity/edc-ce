plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // Logging
    api(libs.edc.monitorJdkLogger)
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
