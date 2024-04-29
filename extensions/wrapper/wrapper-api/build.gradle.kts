val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project
val assertj: String by project
val mockitoVersion: String by project
val lombokVersion: String by project
val jettyVersion: String by project
val jettyGroup: String by project

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.swagger.plugin)  //./gradlew clean resolve
    alias(libs.plugins.hidetake.swaggerGenerator)  //./gradlew generateSwaggerUI
    alias(libs.plugins.openapi.generator)  //./gradlew openApiValidate && ./gradlew openApiGenerate
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(project(":extensions:wrapper:wrapper-common-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":extensions:wrapper:wrapper-ee-api"))

    implementation(libs.jakarta.validationApi)
    implementation(libs.jakarta.rsApi)
    implementation(libs.swagger.annotationsJakarta)
    implementation(libs.swagger.jaxrs2Jakarta)
    implementation(libs.jakarta.servlet)
    implementation(libs.jakarta.validationApi)
    implementation(libs.jakarta.rsApi)
    implementation(libs.apache.commonsLang)
}

val openapiFileDir = "${project.buildDir}/swagger"
val openapiFileFilename = "sovity-edc-api-wrapper.yaml"
val openapiFile = "$openapiFileDir/$openapiFileFilename"

val openapiDocsDir = project.rootProject.rootDir.resolve("docs")

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = java.sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
    resourcePackages = setOf("de.sovity.edc.ext.wrapper.api")
}

tasks.register<Copy>("copyOpenapiYamlToDocs") {
    dependsOn("resolve")
    from(openapiFile)
    into(openapiDocsDir)
}

task<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateTypeScriptClient") {
    dependsOn("resolve")
    dependsOn("copyOpenapiYamlToDocs")
    generatorName.set("typescript-fetch")
    configOptions.set(mutableMapOf(
            "supportsES6" to "true",
            "npmVersion" to "8.15.0",
            "typescriptThreePlus" to "true",
    ))

    inputSpec.set(openapiFile)
    val outputDirectory = buildFile.parentFile.resolve("../clients/typescript-client/src/generated").normalize()
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
