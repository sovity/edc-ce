plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

dependencies {
    // Control-Plane
    implementation(libs.edc.controlPlaneCore)
    implementation(libs.edc.dataPlaneSelectorCore)
    implementation(libs.edc.apiObservability)
    implementation(libs.edc.configurationFilesystem)
    implementation(libs.edc.controlPlaneAggregateServices)
    implementation(libs.edc.http)
    implementation(libs.edc.dsp)
    implementation(libs.edc.jsonLd)

    // JDK Logger
    implementation(libs.edc.monitorJdkLogger)

    // Broker Server + PostgreSQL + Flyway
    implementation(project(":extensions:broker-server"))

    implementation(libs.edc.vaultFilesystem)
    implementation(libs.edc.oauth2Core)
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}
