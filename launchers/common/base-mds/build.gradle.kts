plugins {
    `java-library`
}

dependencies {
    implementation(libs.loggingHouse.client)
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
