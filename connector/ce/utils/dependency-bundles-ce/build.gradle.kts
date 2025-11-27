/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.nio.charset.StandardCharsets

plugins {
    `java-library`
    `maven-publish`
}

buildscript {
    dependencies {
        classpath(libs.squareup.kotlinpoet)
    }
}

/**
 * Defines bundles of EDC Extension JARs
 *
 * Each bundle will become a constant in DependencyBundles.kt
 *
 * You can use these bundles to know which extensions to load, as we have disabled auto-loading of META-INF/services
 * for the EDC.
 */

val packageName = "de.sovity.edc.ce.dependency_bundles"
val className = "CeDependencyBundles"
val classpathResourceSubdir = "dependency-bundles-ce"

val generatedSourcesTarget = project.layout.buildDirectory.file("generated/sources/gradle/main/kotlin")
val generatedResourcesTarget = project.layout.buildDirectory.file("generated/sources/gradle/main/resources")

val dependencyBundles = DependencyBundleList()

dependencyBundles.bundle(
    bundleName = "root",
    bundleDocumentation = "Extensions contained in every EDC"
) {
    api(libs.edc.apiObservability)
    api(libs.edc.jettyCore)
}

dependencyBundles.bundle(
    bundleName = "controlPlaneBase",
    bundleDocumentation = "Extensions for a Control Plane. Basically anything that does not require switching in / out between all variants."
) {
    api(libs.edc.apiCore)
    api(libs.edc.connectorCore)
    api(libs.edc.contractDefinitionApi)
    api(libs.edc.controlApiConfiguration)
    api(libs.edc.controlPlaneApi)
    api(libs.edc.controlPlaneContract)
    api(libs.edc.controlPlaneCore)
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.dataPlaneSelectorControlApi)
    api(libs.edc.dsp)
    api(libs.edc.dspApiConfiguration)
    api(libs.edc.dspHttpCore)
    api(libs.edc.dspNegotiationTransform)
    api(libs.edc.http)
    api(libs.edc.jsonLd)
    api(libs.edc.managementApi)
    api(libs.edc.managementApiConfiguration)
    api(libs.edc.policyDefinitionApi)
    api(libs.edc.secretsApi)
    api(libs.edc.tokenCore)
    api(libs.edc.transferProcessApi)

    // SQL
    api(libs.edc.controlPlaneSql)
    api(libs.edc.dataPlaneInstanceStoreSql)
    api(libs.edc.edrIndexSql)
    api(libs.edc.policyMonitorStoreSql)
    api(libs.edc.sqlCore)

    // additional features
    api(libs.edc.policyMonitorCore)
    api(libs.edc.validatorDataAddressHttpData)
    api(libs.edc.callbackEventDispatcher)
    api(libs.edc.callbackHttpDispatcher)
    api(libs.edc.callbackStaticEndpoint)

    // data-plane-selector
    api(libs.edc.dataPlaneSelectorCore)

    // Data Transfer
    api(libs.edc.transferPullHttpDynamicReceiver)
    api(libs.edc.edrStoreCore)
    api(libs.edc.edrStoreReceiver)
    api(libs.edc.transferDataPlaneSignaling)
    api(libs.edc.azure.provisionBlob)
    api(libs.edc.aws.validatorDataAddressS3)

    // Adds headers Edc-Bpn and Edc-Contract-Agreement-Id to proxied calls via EDRs
    // A Tractus-X dependency, yet compatible with vanilla EDCs for now
    api(libs.tractus.provisionAdditionalHeaders)

    /**
     * Contract Retirement
     *
     * The contract retirement SQL store is replaced by
     * de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge.TractusToSovityContractRetirementBridge
     *
     * see docs/dev/modules/tractus-retirement-to-sovity-termination-bridge.md
     *
     * api(libs.tractus.retirementEvaluationStoreSql)
     *
     * The dependencies below are made available everywhere to re-use the transfer process stopping features from Tractus.
     *
     * Only the `api(libs.tractus.retirementEvaluationApi)` is added in the tractus-specific
     */
    api(libs.tractus.retirementEvaluationCore)
    api(libs.tractus.retirementEvaluationSpi)
}

dependencyBundles.bundle(
    bundleName = "dataPlaneBase",
    bundleDocumentation = "Standalone Data Plane base extensions"
) {
    // Standalone DP
    api(libs.edc.connectorCore)
    api(libs.edc.dataPlaneIam)
    api(libs.edc.http)
    api(libs.edc.jsonLd)
    api(libs.edc.tokenCore)
    api(libs.edc.aws.validatorDataAddressS3)
}

dependencyBundles.bundle(
    bundleName = "dataPlaneFeatures",
    bundleDocumentation = "Common Data Plane extensions for standalone and integrated Data Planes"
) {
    api(libs.edc.controlApiConfiguration)
    api(libs.edc.controlPlaneApiClient)
    api(libs.edc.dataPlaneCore)
    api(libs.edc.dataPlanePublicApiV2)
    api(libs.edc.dataPlaneSignalingApi)

    // transfer-types
    api(libs.edc.dataPlaneHttp)
    api(libs.edc.azure.dataPlaneAzureStorage)
    api(libs.edc.aws.dataPlaneAwsS3)

    // SQL
    api(libs.edc.dataPlaneStoreSql)
    api(libs.edc.accesstokendataStoreSql)

    // OAuth
    api(libs.edc.dataPlaneHttpOauth2)
}

dependencyBundles.bundle(
    bundleName = "managementApiAuthKeycloak",
    bundleDocumentation = "Secure the Management API with our Keycloak OAuth2 Extension",
) {
    api(libs.edc.tokenCore)
    api(libs.edc.authSpi)
    api(libs.edc.oauth2Spi)
    api(libs.edc.httpSpi)

    api(libs.jakarta.rsApi)
    api(libs.nimbus.joseJwt)
}

dependencyBundles.bundle(
    bundleName = "postgresql",
    bundleDocumentation = "Extensions for PostgreSQL"
) {
    api(libs.edc.sqlCore)
    api(libs.edc.sqlPoolApacheCommons)
    api(libs.edc.transactionLocal)

    api(libs.flyway.core)
    api(libs.postgres)
    api(libs.testcontainers.testcontainers)
    api(libs.testcontainers.postgresql)
}

dependencyBundles.bundle(
    bundleName = "c2cIamMock",
    bundleDocumentation = "Extensions for C2C IAM Type: Mock"
) {
    api(libs.edc.iamMock)
}

dependencyBundles.bundle(
    bundleName = "c2cIamDapsSovity",
    bundleDocumentation = "Extensions for C2C IAM Type: DAPS"
) {
    api(libs.edc.oauth2Core)
}

dependencyBundles.bundle(
    bundleName = "c2cIamDapsOmejdn",
    bundleDocumentation = "Extensions for C2C IAM Type: DAPS"
) {
    api(libs.edc.oauth2Core)
    api(libs.edc.oauth2Daps)
}

dependencyBundles.bundle(
    bundleName = "managementApiAuthApiKey",
    bundleDocumentation = "Extension for Management API Auth via an API Key"
) {
    api(libs.edc.authTokenbased)
}

dependencyBundles.bundle(
    bundleName = "micrometer",
    bundleDocumentation = "Micrometer metrics and tracing for the EDC"
) {
    // Observability API
    api(libs.edc.apiObservability)

    // micrometer
    api(libs.edc.micrometerCore)
    api(libs.edc.jerseyMicrometer)
    api(libs.edc.jerseyProviders)
    api(libs.edc.jettyMicrometer)
}

dependencyBundles.bundle(
    bundleName = "sovityControlPlane",
    bundleDocumentation = "sovity Control Plane"
) {
    // Vanilla EDRs
    api(libs.edc.edrCacheApi)
}

dependencyBundles.bundle(
    bundleName = "sphinxControlPlane",
    bundleDocumentation = "sphin-X Control Plane"
) {
    api(libs.edc.transferDataPlaneSignaling)
    api(libs.edc.azure.provisionBlob)
    api(libs.tractus.jsonLdCore)

    // iatp
    api(libs.edc.identityDidCore)
    api(libs.edc.identityDidWeb)
    api(libs.edc.identityTrustCore)
    api(libs.edc.identityTrustIssuersConfiguration)
    api(libs.edc.identityTrustTransform)

    // iatp tx
    api(libs.tractus.txDcp)
    api(libs.tractus.txDcpStsDim)

    // Vanilla EDRs
    api(libs.edc.edrCacheApi)

    // Adds headers Edc-Bpn and Edc-Contract-Agreement-Id to the http-push-transfer when invoking the target backend
    api(libs.tractus.provisionAdditionalHeaders)
}

dependencyBundles.bundle(
    bundleName = "tractusControlPlane",
    bundleDocumentation = "Tractus Control Plane"
) {
    api(libs.edc.transferDataPlaneSignaling)
    api(libs.edc.azure.provisionBlob)
    api(libs.tractus.jsonLdCore)
    api(libs.tractus.emptyAssetSelector)

    // iatp
    api(libs.edc.identityDidCore)
    api(libs.edc.identityDidWeb)
    api(libs.edc.identityTrustCore)
    api(libs.edc.identityTrustIssuersConfiguration)
    api(libs.edc.identityTrustTransform)

    // iatp tx
    api(libs.tractus.tokenrefreshHandler)
    api(libs.tractus.txDcp)
    api(libs.tractus.txDcpStsDim)

    // edr tx
    api(libs.tractus.edrCore)
    api(libs.tractus.edrApiV2)
    api(libs.tractus.edrCallback)
    api(libs.tractus.tokenrefreshHandler)
    api(libs.tractus.dataFlowPropertiesProvider)
    api(libs.tractus.bdrsClient)

    // Adds headers Edc-Bpn and Edc-Contract-Agreement-Id to the http-push-transfer when invoking the target backend
    api(libs.tractus.provisionAdditionalHeaders)

    // Adds a policy, checking the referringConnector claim of the dat if it contains the correct business-partner-number
    api(libs.tractus.bpnValidation)

    // Includes a summary credential in the token parameters
    api(libs.tractus.cxPolicy)

    // sql extensions
    api(libs.tractus.businessPartnerStoreSql)
    api(libs.tractus.edrIndexLockSql)

    api(libs.tractus.retirementEvaluationApi)
}

dependencyBundles.bundle(
    bundleName = "tractusDataPlane",
    bundleDocumentation = "Tractus Data Plane"
) {
    api(libs.edc.edrStoreCore)
    api(libs.edc.identityDidCore)
    api(libs.edc.identityDidWeb)
    api(libs.tractus.edcDataplaneProxyConsumerApi)
    api(libs.tractus.edrCore)
    api(libs.tractus.tokenRefreshApi)
    api(libs.tractus.tokenRefreshCore)
    api(libs.tractus.tokenrefreshHandler)
    api(libs.tractus.txDcpStsDim)

    // sql
    api(libs.edc.edrIndexSql)
    api(libs.edc.assetIndexSql)
}

dependencyBundles.bundle(
    bundleName = "azureVault",
    bundleDocumentation = "Azure vault support"
) {
    api(libs.edc.azure.vaultAzure)
}

dependencyBundles.bundle(
    bundleName = "hashicorpVault",
    bundleDocumentation = "Hashicorp vault support"
) {
    api(libs.edc.vaultHashicorp)
}

dependencyBundles.bundle(
    bundleName = "testBackend",
    bundleDocumentation = "Minimal EDC with Default HTTP Server"
) {
    api(libs.edc.apiCore)
    api(libs.edc.boot)
    api(libs.edc.http)
    api(libs.edc.jsonLd)
    api(libs.edc.connectorCore)
}

dependencies {
    // lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // bundles
    dependencyBundles.getBundles().forEach { bundle ->
        bundle.dependencies.forEach { dependency ->
            // Include all as "api"
            api(dependency)

            // Include in configuration
            bundle.gradleConfiguration(dependency)
        }
    }

    // tests
    testImplementation(libs.junit.api)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.engine)
}

val generateDependencyBundlesSources: Task by tasks.creating {
    sourceSets["main"].kotlin.srcDir(generatedSourcesTarget)

    doLast {
        val dependencyBundlesType = TypeSpec.objectBuilder(className)
            .addKdoc("List of available ServiceClass bundles\n\nGenerated by our build.gradle.kts")

        fun dependencyToString(dependency: Any): String = when (dependency) {
            is String -> dependency
            is ProjectDependency -> "project(':${dependency.name}')"
            is FileCollection -> dependency.files.joinToString { it.absolutePath }
            is Dependency -> "${dependency.group}:${dependency.name}:${dependency.version}"
            is Provider<*> -> dependencyToString(dependency.get())
            else -> error("Unhandled ${dependency::class}")
        }

        fun String.toStringLiteral(): String =
            '"' + replace("\\", "\\\\").replace("\$", "\\\$").replace("\"", "\\\"") + '"'

        fun List<String>.toSetOfStringStatement(): String =
            "setOf<String>(${joinToString { it.toStringLiteral() }})"

        dependencyBundles.getBundles().forEach { bundle ->
            val dependencyLines = bundle.dependencies
                .map { dependencyToString(it) }
                .sorted()
                .joinToString("\n") { " - $it" }
            val docs = "${bundle.documentation}\n$dependencyLines"
            val jarsSetOf = bundle.dependencies
                .map { dependencyToString(it) }
                .distinct()
                .sorted()
                .toSetOfStringStatement()

            val dependencyBundleClass = "de.sovity.edc.runtime.modules.dependency_bundles.DependencyBundle"

            PropertySpec.builder(bundle.bundleName, ClassName.bestGuess(dependencyBundleClass))
                .initializer(
                    "${dependencyBundleClass}(%S, %S, $jarsSetOf, %S)",
                    bundle.bundleName,
                    bundle.documentation,
                    classpathResourceSubdir
                )
                .addKdoc(docs)
                .build()
                .also { dependencyBundlesType.addProperty(it) }
        }

        val fileSpec = FileSpec.builder(packageName, className)
            .addType(dependencyBundlesType.build())
            .build()

        fileSpec.writeTo(file(generatedSourcesTarget))
    }
}

val generateDependencyBundlesResources: Task by tasks.creating {
    outputs.cacheIf { true }
    outputs.dir(generatedResourcesTarget)

    dependencyBundles.getBundles().forEach { bundle ->
        inputs.files(*bundle.gradleConfiguration.files.toTypedArray())
    }

    dependencyBundles.getBundles().forEach { bundle ->
        dependsOn(bundle.gradleConfiguration)
    }

    sourceSets["main"].resources.srcDir(generatedResourcesTarget)

    doLast {
        dependencyBundles.getBundles().forEach { bundle ->
            val bundleDirectory = generatedResourcesTarget.get().asFile
                .resolve(classpathResourceSubdir)
                .resolve(bundle.bundleName)
                .also { it.mkdirs() }

            // Collect and deduplicate services
            // Map: Service File -> Service Class -> Source JARs[]
            val serviceFiles = mutableMapOf<String, MutableMap<String, MutableSet<String>>>()

            bundle.gradleConfiguration.files.forEach { dependency ->
                zipTree(dependency).matching {
                    include("META-INF/services/**")
                }.forEach { serviceFile ->
                    // read service file contents
                    val serviceFileContents = serviceFile.inputStream()
                        .bufferedReader(StandardCharsets.UTF_8)
                        .use { it.readText() }

                    fun File.relativePathAfter(after: String): String =
                        toString().replace(File.separator, "/").substringAfter(after)

                    val serviceFileMap = serviceFiles.computeIfAbsent(serviceFile.name) { mutableMapOf() }

                    serviceFileContents.split("\n")
                        .filter { !it.startsWith("#") && it.isNotBlank() }
                        .distinct()
                        .forEach { serviceClass ->
                            serviceFileMap.computeIfAbsent(serviceClass) { mutableSetOf() }
                                .add(dependency.relativePathAfter("caches/modules-2/files-2.1/"))
                        }
                }
            }

            serviceFiles.forEach { (serviceFile, services) ->
                val serviceFileContents = services.entries
                    .groupBy { (_, sources) -> sources }
                    .entries
                    .joinToString("\n\n") { (sources, serviceClasses) ->
                        val from = sources.sorted().joinToString("\n") { "# From: $it" }
                        "$from\n${serviceClasses.joinToString("\n") { it.key }}"
                    }
                File(bundleDirectory, serviceFile).writeText(serviceFileContents)
            }
        }
    }
}

val generateDependencyBundles: Task by tasks.creating {
    group = "sovity"
    description = "Merge META-INF/services for each bundle and generate ${className}.kt"

    dependsOn(generateDependencyBundlesSources)
    dependsOn(generateDependencyBundlesResources)
}

tasks.getByName("compileKotlin") {
    dependsOn(generateDependencyBundlesSources)
}

tasks.getByName("compileJava") {
    dependsOn(generateDependencyBundlesSources)
}

tasks.getByName("processResources") {
    dependsOn(generateDependencyBundlesResources)
}

tasks.withType<Delete>().named("clean") {
    doLast {
        delete(generatedSourcesTarget)
        delete(generatedResourcesTarget)
    }
}


class DependencyBundleList {
    private val dependencyBundles = mutableListOf<DependencyBundle>()
    fun bundle(
        bundleName: String,
        bundleDocumentation: String,
        applyFn: DependencyBundle.() -> Unit
    ) = also {
        DependencyBundle(bundleName, bundleDocumentation)
            .apply(applyFn)
            .also { dependencyBundles.add(it) }
    }

    fun getBundles() = dependencyBundles.toList()
}

class DependencyBundle(val bundleName: String, val documentation: String) {
    private val camelCase = Regex("^[a-z][A-Za-z0-9]*$")
    private val dependencyNotations = mutableListOf<Any>()

    /**
     * Gradle Configuration for a classpath of just this bundle
     */
    val gradleConfiguration = configurations.create(bundleName)

    /**
     * List of Dependencies in this Bundle
     */
    val dependencies: List<Any> get() = dependencyNotations.toList()

    init {
        require(camelCase.matches(bundleName)) {
            "Bundle name '$bundleName' must be camelCase. They will become fields of a Kotlin Object. We want to allow for discovery via Ctrl+Shift+F, so we want the same format."
        }
    }

    fun api(dependencyNotation: Any) = also {
        dependencyNotations.add(dependencyNotation)
    }
}

group = libs.versions.sovityCeGroupName.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

