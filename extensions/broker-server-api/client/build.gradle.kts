
plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.openapi.generator7)
}

repositories {
    mavenCentral()
}

// By using a separate configuration we can skip having the Extension Jar in our runtime classpath
val openapiYaml = configurations.create("openapiGenerator")

dependencies {
    // We only need the openapi.yaml file from this dependency
    openapiYaml(project(":extensions:broker-server-api:api")) {
        isTransitive = false
    }

    // Generated Client's Dependencies
    implementation(libs.swagger.annotations)
    implementation(libs.findbugs.jsr305)
    implementation(libs.okhttp.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.gson)
    implementation(libs.gsonFire)
    implementation(libs.openapi.jacksonDatabindNullable)
    implementation(libs.apache.commonsLang)
    implementation(libs.jakarta.annotationApi)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// Extract the openapi file from the JAR
val openapiFileName = "broker-server.yaml"
val targetLocation = project.buildDir.resolve("openapi")
val extractOpenapiYaml by tasks.registering(Copy::class) {
    dependsOn(openapiYaml)
    into(targetLocation)
    from(zipTree(openapiYaml.singleFile)) {
        include(openapiFileName)
    }
}

val openApiGenerate = tasks.getByName<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    dependsOn(extractOpenapiYaml)
    generatorName.set("java")
    configOptions.set(
        mutableMapOf(
            "invokerPackage" to "de.sovity.edc.ext.brokerserver.client.gen",
            "apiPackage" to "de.sovity.edc.ext.brokerserver.client.gen.api",
            "modelPackage" to "de.sovity.edc.ext.brokerserver.client.gen.model",
            "caseInsensitiveResponseHeaders" to "true",
            "additionalModelTypeAnnotations" to "@lombok.AllArgsConstructor\n@lombok.Builder",
            "annotationLibrary" to "swagger1",
            "hideGenerationTimestamp" to "true",
            "useRuntimeException" to "true",
        )
    )

    inputSpec.set(targetLocation.resolve(openapiFileName).path)
    outputDir.set("${project.buildDir}/generated/client-project")
}

val postprocessGeneratedClient by tasks.registering(Copy::class) {
    dependsOn(openApiGenerate)
    from("${project.buildDir}/generated/client-project/src/main/java")

    // @lombok.Builder clashes with the following generated model file.
    // It is the base class for OAS3 polymorphism via allOf/anyOf, which we won't use anyway.
    exclude("**/AbstractOpenApiSchema.java")

    // The Jax-RS dependency suggested by the generated project was causing issues with quarkus.
    // It was again only required for the polymorphism, which we won't use anyway.
    filter { if (it == "import javax.ws.rs.core.GenericType;") "" else it }

    into("${project.buildDir}/generated/sources/openapi/java/main")
}
sourceSets["main"].java.srcDir("${project.buildDir}/generated/sources/openapi/java/main")

checkstyle {
    // Checkstyle loathes the generated files
    // TODO make checkstyle skip generated files only
    this.sourceSets = emptyList()
}


tasks.getByName<JavaCompile>("compileJava") {
    dependsOn(postprocessGeneratedClient)
}

val sourcesJar = tasks.getByName<Jar>("sourcesJar") {
    dependsOn(postprocessGeneratedClient)
}

val javadocJar = tasks.getByName<Jar>("javadocJar") {
    dependsOn(postprocessGeneratedClient)
}

artifacts {
    add("archives", sourcesJar)
    add("archives", javadocJar)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Javadoc> {
    val fullOptions = this.options as StandardJavadocDocletOptions
    fullOptions.tags = listOf("http.response.details:a:Http Response Details")
    fullOptions.addStringOption("Xdoclint:none", "-quiet")
}

group = libs.versions.sovityBrokerServerGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
