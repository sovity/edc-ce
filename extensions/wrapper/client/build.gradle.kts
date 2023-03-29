val edcVersion: String by project
val edcGroup: String by project
val restAssured: String by project

val edcClientGroup: String by project
group = edcClientGroup


plugins {
    `java-library`
    `maven-publish`
    id("org.openapi.generator") version "6.3.0"
}

repositories {
    mavenCentral()
}

// By using a separate configuration we can skip having the Java Code in our classpath
val openapiYaml = configurations.create("openapiGenerator")

dependencies {
    // We only need the openapi.yaml file from this dependency
    openapiYaml(project(":extensions:wrapper:wrapper")) {
        isTransitive = false
    }

    // Generated Client's Dependencies
    implementation("io.swagger:swagger-annotations:1.6.8")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.gsonfire:gson-fire:1.8.5")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testImplementation("${edcGroup}:control-plane-core:${edcVersion}")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("${edcGroup}:http:${edcVersion}")
    testImplementation(project(":extensions:wrapper:wrapper"))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// Extract the openapi file from the JAR
val openapiFile = "edc-api-wrapper.yaml"
task<Copy>("extractOpenapiYaml") {
    dependsOn(openapiYaml)
    into("${project.buildDir}")
    from(zipTree(openapiYaml.singleFile)) {
        include("edc-api-wrapper.yaml")
    }
}

tasks.getByName<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
    dependsOn("extractOpenapiYaml")
    generatorName.set("java")
    configOptions.set(mutableMapOf(
            "invokerPackage" to "de.sovity.edc.client.gen",
            "apiPackage" to "de.sovity.edc.client.gen.api",
            "modelPackage" to "de.sovity.edc.client.gen.model",
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

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
