
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.edc.policyModel)
    api(libs.edc.coreSpi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.transformSpi)
    api(libs.edc.jsonLdLib)
    api(libs.edc.jsonLdSpi)
    api(libs.edc.assetSpi)

    api(project(":extensions:wrapper:wrapper-common-api"))
    api(project(":utils:json-and-jsonld-utils"))
    implementation(project(":config"))

    implementation(libs.apache.commonsLang)
    implementation(libs.apache.commonsCollections)
    implementation(libs.flexmark.all)
    implementation(libs.okhttp.okhttp)


    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junitJupiter)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityEdcGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
