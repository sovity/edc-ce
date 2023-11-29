plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // Logging
    api("${edcGroup}:monitor-jdk-logger:${edcVersion}")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
