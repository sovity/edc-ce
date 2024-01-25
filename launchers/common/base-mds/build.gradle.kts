plugins {
    `java-library`
}

dependencies {
    implementation("logging-house:logging-house-client:0.1.1")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
