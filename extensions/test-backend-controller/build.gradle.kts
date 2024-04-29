val edcVersion: String by project
val edcGroup: String by project

plugins {
    `java-library`
}

dependencies {
    api(libs.edc.apiCore)
    api(libs.edc.coreSpi)
    api(libs.edc.http)
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
