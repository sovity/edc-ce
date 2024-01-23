plugins {
    `java-library`
}

dependencies {
    implementation("logging-house:logging-house-client:0.1.0")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
