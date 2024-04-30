val sovityEdcGroup: String by project
val sovityEdcExtensionsVersion: String by project

plugins {
    `java-library`
    `maven-publish`
    id("io.swagger.core.v3.swagger-gradle-plugin") version "2.2.18" //./gradlew clean resolve
    id("org.hidetake.swagger.generator") version "2.19.2" //./gradlew generateSwaggerUI
    id("org.openapi.generator") version "7.0.1" //./gradlew openApiValidate && ./gradlew openApiGenerate
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(project(":extensions:wrapper:wrapper-common-api"))

    api(libs.jakarta.rsApi)
    api(libs.jakarta.validationApi)
    api(libs.swagger.annotationsJakarta)
    api(libs.swagger.jaxrs2Jakarta)
    api(libs.jakarta.servletApi)

    implementation(libs.apache.commonsLang)
    implementation(libs.jakarta.validationApi)
    // TODO: duplication between api and implementation?!
    implementation(libs.jakarta.rsApi)
    implementation(libs.swagger.annotationsJakarta)
    implementation(libs.swagger.jaxrs2Jakarta)
    implementation(libs.jakarta.servletApi)
    implementation(libs.jakarta.validationApi)
    implementation(libs.apache.commonsLang)
}

val openapiFileDir = "${project.buildDir}/swagger"
val openapiFileFilename = "broker-server.yaml"
val openapiFile = "$openapiFileDir/$openapiFileFilename"

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = java.sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
    resourcePackages = setOf("")
}

task<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateTypeScriptClient") {
    validateSpec.set(false)
    dependsOn("resolve")
    generatorName.set("typescript-fetch")
    configOptions.set(mutableMapOf(
        "supportsES6" to "true",
        "npmVersion" to "8.15.0",
        "typescriptThreePlus" to "true",
    ))

    inputSpec.set(openapiFile)
    val outputDirectory = buildFile.parentFile.resolve("../client-ts/src/generated").normalize()
    outputDir.set(outputDirectory.toString())

    doFirst {
        project.delete(fileTree(outputDirectory).exclude("**/.gitignore"))
    }

    doLast {
        outputDirectory.resolve("src/generated").renameTo(outputDirectory)
    }
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    dependsOn("resolve")
    dependsOn("openApiGenerateTypeScriptClient")
    from(openapiFileDir) {
        include(openapiFileFilename)
    }
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
