plugins {
    `java-library`
    `maven-publish`
}

val edcVersion: String by project
val edcGroup: String by project
val theGroup: String by project
val theVersion: String by project

group = theGroup
version = theVersion

dependencies {
    implementation("${edcGroup}:core-spi:${edcVersion}")
    implementation("${edcGroup}:contract-spi:${edcVersion}")
    implementation("${edcGroup}:control-plane-spi:${edcVersion}")
    implementation(project(":extensions:catalog-transfer-extension"))
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}