val edcVersion: String by project
val edcGroup: String by project

plugins {
    `java-library`
}

dependencies {
    api("${edcGroup}:api-core:${edcVersion}")
    api("${edcGroup}:core-spi:${edcVersion}")
    api("${edcGroup}:http:${edcVersion}")
}

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}