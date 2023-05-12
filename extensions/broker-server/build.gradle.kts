plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project
val mockitoVersion: String by project
val assertj: String by project
val okHttpVersion: String by project
val sovityEdcGroup: String by project
val sovityEdcExtensionsVersion: String by project

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:management-api-configuration:${edcVersion}")
    implementation("${edcGroup}:ids-spi:${edcVersion}")
    implementation("${edcGroup}:ids-api-multipart-dispatcher-v1:${edcVersion}")
    implementation("${edcGroup}:ids-api-configuration:${edcVersion}")
    implementation("${edcGroup}:ids-jsonld-serdes:${edcVersion}")

    api("${sovityEdcGroup}:wrapper-broker-api:${sovityEdcExtensionsVersion}")

    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
