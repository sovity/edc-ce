plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":extensions:catalog-crawler:catalog-crawler-launcher-base"))

    api(libs.edc.monitorJdkLogger)
    api(libs.edc.apiObservability)

    implementation(project(":launchers:common:auth-mock"))
}

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("app.jar")
}
