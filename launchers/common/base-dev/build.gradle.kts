plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // Mock IAM
    api("${edcGroup}:iam-mock:${edcVersion}")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
