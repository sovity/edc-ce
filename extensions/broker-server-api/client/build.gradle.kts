val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project
val assertj: String by project


plugins {
    `java-library`
    `maven-publish`
    id("org.openapi.generator") version "7.0.1"
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
val openapiFile = "broker-server.yaml"
task<Copy>("extractOpenapiYaml") {
    dependsOn(openapiYaml)
    into("${project.buildDir}")
    from(zipTree(openapiYaml.singleFile)) {
        include("broker-server.yaml")
    }
}

tasks.getByName<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    dependsOn("extractOpenapiYaml")
    generatorName.set("java")
    configOptions.set(mutableMapOf(
            "invokerPackage" to "de.sovity.edc.ext.brokerserver.client.gen",
            "apiPackage" to "de.sovity.edc.ext.brokerserver.client.gen.api",
            "modelPackage" to "de.sovity.edc.ext.brokerserver.client.gen.model",
            "caseInsensitiveResponseHeaders" to "true",
            "additionalModelTypeAnnotations" to "@lombok.AllArgsConstructor\n@lombok.Builder",
            "annotationLibrary" to "swagger1",
            "hideGenerationTimestamp" to "true",
            "useRuntimeException" to "true",
    ))

    inputSpec.set("${project.buildDir}/${openapiFile}")
    outputDir.set("${project.buildDir}/generated/client-project")
}

task<Copy>("postprocessGeneratedClient") {
    dependsOn("openApiGenerate")
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
    dependsOn("postprocessGeneratedClient")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Javadoc> {
    val fullOptions = this.options as StandardJavadocDocletOptions
    fullOptions.tags = listOf("http.response.details:a:Http Response Details")
    fullOptions.addStringOption("Xdoclint:none", "-quiet")
}

val sovityBrokerServerGroup: String by project
group = sovityBrokerServerGroup

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
