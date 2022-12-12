plugins {
    `java-library`
}

val edcVersion: String by project
val edcGroup: String by project
val jupiterVersion: String by project
val assertj: String by project
val identityHubGroup: String by project
val identityHubVersion: String by project

dependencies {
    api("${edcGroup}:ids-spi:${edcVersion}")
    api("${edcGroup}:contract-spi:${edcVersion}")
    api("${edcGroup}:control-plane-core:${edcVersion}")
    api("${edcGroup}:policy-engine:${edcVersion}")
    implementation("${identityHubGroup}:identity-hub-spi:${identityHubVersion}")

    testImplementation("org.assertj:assertj-core:${assertj}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")
}