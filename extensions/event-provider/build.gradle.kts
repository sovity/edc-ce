plugins {
    `java-library`
}
tasks.register("prepareKotlinBuildScriptModel"){}
val edcVersion: String by project
val edcGroup: String by project

dependencies {
    implementation("${edcGroup}:core-spi:${edcVersion}")
    implementation("${edcGroup}:contract-spi:${edcVersion}")
    implementation("${edcGroup}:control-plane-spi:${edcVersion}")
    implementation(project(":extensions:broker"))
    implementation(project(":extensions:clearinghouse"))
}