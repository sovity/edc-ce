val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project
val mockitoVersion: String by project

plugins {
    `java-library`
}

dependencies {
    api("${edcGroup}:core-spi:${edcVersion}")
    api("${edcGroup}:control-plane-spi:${edcVersion}")
    api("${edcGroup}:transfer-spi:${edcVersion}")
    implementation("${edcGroup}:api-core:${edcVersion}")
    implementation("${edcGroup}:asset-api:${edcVersion}")
    implementation("${edcGroup}:management-api-configuration:${edcVersion}")
    implementation("${edcGroup}:transfer-process-api:${edcVersion}")

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}")
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup
