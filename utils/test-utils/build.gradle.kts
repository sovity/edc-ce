val edcVersion: String by project
val edcGroup: String by project
val testcontainersVersion: String by project
val lombokVersion: String by project
val restAssured: String by project
val awaitilityVersion: String by project
val assertj: String by project

plugins {
    `java-library`
}

dependencies {
    api("org.junit.jupiter:junit-jupiter-api:5.10.0")
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
