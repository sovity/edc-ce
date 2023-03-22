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
    api("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    api("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.2")
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    swaggerUI("org.webjars:swagger-ui:4.18.1")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("io.swagger.core.v3:swagger-jaxrs2-jakarta:2.2.9")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
}

swaggerSources {
    register("wrapper").configure {
        setInputFile(file("${project.buildDir}/swagger/edcapiwrapper.yaml"))
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file("$buildDir/swagger")
    outputFileName = "edcapiwrapper"
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = java.sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("${project.buildDir}/swagger/edcapiwrapper.yaml")
}

openApiValidate {
    inputSpec.set("${project.buildDir}/swagger/edcapiwrapper.yaml")
}