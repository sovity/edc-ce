plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project

dependencies {
    // OAuth2 IAM
    api("${edcGroup}:oauth2-core:${edcVersion}")
    api("${edcGroup}:vault-filesystem:${edcVersion}")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
