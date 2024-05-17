
plugins {
    `java-library`
    `maven-publish`
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
    implementation(libs.jersey.mediaMultipart)
}

group = libs.versions.sovityEdcGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
