val edcVersion: String by project
val edcGroup: String by project

plugins {
    `java-library`
}

dependencies {
    implementation("${edcGroup}:http:${edcVersion}")
}

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup

