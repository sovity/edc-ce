val edcVersion: String by project
val edcGroup: String by project
val mockitoVersion: String by project
val jupiterVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api("${edcGroup}:auth-spi:${edcVersion}")
    api("${edcGroup}:policy-engine-spi:${edcVersion}")
    api("${edcGroup}:contract-spi:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")

    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
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
