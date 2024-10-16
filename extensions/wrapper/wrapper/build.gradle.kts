
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":config"))
    api(project(":extensions:wrapper:wrapper-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))

    api(libs.edc.contractDefinitionApi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.assetSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.policyDefinitionApi)
    api(libs.edc.transferProcessApi)

    implementation(project(":extensions:contract-termination"))
    implementation(project(":extensions:database-direct-access"))
    implementation(project(":extensions:sovity-messenger"))
    implementation(project(":utils:jooq-database-access"))

    implementation(libs.apache.commonsLang)
    implementation(libs.edc.apiCore)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.dspApiConfiguration)
    implementation(libs.edc.dspHttpSpi)
    implementation(libs.jooq.jooq)
    implementation(libs.hibernate.validation)
    implementation(libs.hikari)
    implementation(libs.jakarta.el)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:policy-always-true"))
    testImplementation(project(":extensions:postgres-flyway"))
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.dsp)
    testImplementation(libs.edc.iamMock)
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.http)

    testImplementation(libs.edc.controlPlaneSql)
    testImplementation(libs.edc.contractNegotiationStoreSql)
    testImplementation(libs.edc.dspHttpSpi)
    testImplementation(libs.edc.dspApiConfiguration)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.edc.transferProcessStoreSql)

    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
