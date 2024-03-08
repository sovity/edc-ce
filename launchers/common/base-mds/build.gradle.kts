plugins {
    `java-library`
}

dependencies {
    implementation("logging-house:logging-house-client:0.2.8")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
