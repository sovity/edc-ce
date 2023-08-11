plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val assertj: String by project

dependencies {
    // Control-Plane
    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:management-api:${edcVersion}")
    implementation("${edcGroup}:api-observability:${edcVersion}")
    implementation("${edcGroup}:configuration-filesystem:${edcVersion}")
    implementation("${edcGroup}:control-plane-aggregate-services:${edcVersion}")
    implementation("${edcGroup}:http:${edcVersion}")
    implementation("${edcGroup}:dsp:${edcVersion}")
    implementation("${edcGroup}:json-ld:${edcVersion}")
    implementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")

    // Control-plane to Data-plane
    implementation("${edcGroup}:transfer-data-plane:${edcVersion}")
    implementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    implementation("${edcGroup}:data-plane-selector-client:${edcVersion}")

    // Data-plane
    implementation("${edcGroup}:data-plane-http:${edcVersion}")
    implementation("${edcGroup}:data-plane-framework:${edcVersion}")
    implementation("${edcGroup}:data-plane-core:${edcVersion}")
    implementation("${edcGroup}:data-plane-util:${edcVersion}")

    // JDK Logger
    implementation("${edcGroup}:monitor-jdk-logger:${edcVersion}")

    // sovity Extensions Package
    implementation(project(":extensions:sovity-edc-extensions-package"))

    testImplementation(project(":e2e-test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("${edcGroup}:junit:${edcVersion}")
    testImplementation("org.assertj:assertj-core:${assertj}")


    testImplementation(project(":extensions:postgres-flyway"))
    testImplementation("${edcGroup}:iam-mock:${edcVersion}")
    testImplementation("${edcGroup}:auth-tokenbased:${edcVersion}")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
