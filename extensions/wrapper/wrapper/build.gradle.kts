val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project

plugins {
    `java-library`
    `maven-publish`
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.2.9" //./gradlew clean resolve
    id("org.hidetake.swagger.generator") version "2.19.2" //./gradlew generateSwaggerUI
    id("org.openapi.generator") version "6.4.0" //./gradlew openApiValidate && ./gradlew openApiGenerate
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")

    implementation("${edcGroup}:api-core:${edcVersion}")
    implementation("${edcGroup}:management-api-configuration:${edcVersion}")
    api("${edcGroup}:contract-definition-api:${edcVersion}")
    api("${edcGroup}:control-plane-spi:${edcVersion}")
    api("${edcGroup}:core-spi:${edcVersion}")
    api("${edcGroup}:policy-definition-api:${edcVersion}")
    api("${edcGroup}:transfer-process-api:${edcVersion}")

    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.9")
    implementation("io.swagger.core.v3:swagger-jaxrs2-jakarta:2.2.9")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}")
    testImplementation(project(":extensions:policy-always-true"))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

val openapiFileDir = "${project.buildDir}/swagger"
val openapiFileFilename = "edc-api-wrapper.yaml"
val openapiFile = "$openapiFileDir/$openapiFileFilename"

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = java.sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
    resourcePackages = setOf("de.sovity.edc.ext.wrapper.api")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    dependsOn("resolve")
    from(openapiFileDir) {
        include(openapiFileFilename)
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
