import com.diffplug.gradle.spotless.SpotlessTask

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.swagger.plugin)  //./gradlew clean resolve
    alias(libs.plugins.hidetake.swaggerGenerator)  //./gradlew generateSwaggerUI
    alias(libs.plugins.openapi.generator6)  //./gradlew openApiValidate && ./gradlew openApiGenerate
    alias(libs.plugins.spotless)
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(project(":extensions:wrapper:wrapper-common-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":extensions:wrapper:wrapper-ee-api"))

    implementation(libs.apache.commonsLang)
    implementation(libs.hibernate.validation)
    implementation(libs.jakarta.el)
    implementation(libs.jakarta.rsApi)
    implementation(libs.jakarta.servletApi)
    implementation(libs.jakarta.validationApi)
    implementation(libs.swagger.annotationsJakarta)
    implementation(libs.swagger.jaxrs2Jakarta)
}

val openapiFileDir = "${project.buildDir}/swagger"
val openapiFileFilename = "sovity-edc-api-wrapper.yaml"
val openapiFile = "$openapiFileDir/$openapiFileFilename"

val openapiDocsDir = project.rootProject.rootDir.resolve("docs").resolve("api")

tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = java.sourceSets["main"].runtimeClasspath
    buildClasspath = classpath
    resourcePackages = setOf("de.sovity.edc.ext.wrapper.api")
}

spotless {
    yaml {
        target("src/**/*.yaml")
        jackson()
            .feature("ORDER_MAP_ENTRIES_BY_KEYS", true)
    }
}

val copyOpenapiYamlToDocs by tasks.registering(Copy::class) {
    dependsOn("resolve")
    from(openapiFile)
    into(openapiDocsDir)
}

val openApiGenerateTypeScriptClient by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    dependsOn("resolve")
    dependsOn(copyOpenapiYamlToDocs)
    generatorName.set("typescript-fetch")
    configOptions.set(
        mutableMapOf(
            "supportsES6" to "true",
            "npmVersion" to "8.15.0",
            "typescriptThreePlus" to "true",
        )
    )

    inputSpec.set(openapiFile)
    val outputDirectory = buildFile.parentFile.resolve("../clients/typescript-client/src/generated").normalize()
    outputDir.set(outputDirectory.toString())

    doFirst {
        project.delete(fileTree(outputDirectory).exclude("**/.gitignore"))
    }

    doLast {
        outputDirectory.resolve("src/generated").renameTo(outputDirectory)
    }

    finalizedBy(tasks.withType<SpotlessTask>())
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    dependsOn("resolve")
    dependsOn(openApiGenerateTypeScriptClient)
    from(openapiFileDir) {
        include(openapiFileFilename)
    }
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
