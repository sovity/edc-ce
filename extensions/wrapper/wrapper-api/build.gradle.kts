plugins {
    `java-library`
    `maven-publish`
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.2.8" //./gradlew clean resolve
    id("org.hidetake.swagger.generator") version "2.19.2" //./gradlew generateSwaggerUI
    id("org.openapi.generator") version "6.3.0" //./gradlew openApiValidate && ./gradlew openApiGenerate
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    compileOnly("org.projectlombok:lombok:1.18.26")
    api("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    api("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.8")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("io.swagger.core.v3:swagger-jaxrs2-jakarta:2.2.9")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
}

val openapiFileDir = "${project.buildDir}/swagger"
val openapiFileFilename = "edc-api-wrapper.yaml"
val openapiFile = "$openapiFileDir/$openapiFileFilename"

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = java.sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    dependsOn("resolve")
    from(openapiFileDir) {
        include(openapiFileFilename)
    }
}

