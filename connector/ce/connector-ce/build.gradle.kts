/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.eclipse.edc.spi.query.CriterionOperatorRegistry
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.reflect.KClass

plugins {
    `java-library`
    alias(libs.plugins.taskinfo)
    `maven-publish`
}

buildscript {
    repositories {
        maven {
            url =
                uri("https://pkgs.dev.azure.com/sovity/41799556-91c8-4df6-8ddb-4471d6f15953/_packaging/core-edc/maven/v1")
            name = "AzureRepo"
        }
        mavenLocal()
    }

    dependencies {
        classpath(libs.squareup.kotlinpoet)
        classpath(libs.edc.coreSpi)
    }
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    // Core EDC Extensions
    api(project(":ce:utils:dependency-bundles-ce"))

    // Core EDC SPIs
    api(libs.edc.assetSpi)
    api(libs.edc.authSpi)
    api(libs.edc.contractSpi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.dspHttpSpi)
    api(libs.edc.httpLib)
    api(libs.edc.httpSpi)
    api(libs.edc.keysLib)
    api(libs.edc.managementApiLib)
    api(libs.edc.oauth2Spi)
    api(libs.edc.participantSpi)
    api(libs.edc.policyEngineSpi)
    api(libs.edc.sqlLib)
    api(libs.edc.transferSpi)

    // Tractus-X SPIs
    api(libs.tractus.edrSpi)

    // Core EDC LIBs
    api(libs.edc.bootLib)
    api(libs.edc.transformLib)

    api(project(":ce:libs:api"))
    api(project(":ce:libs:jsonld-lib"))
    api(project(":ce:libs:mappers-lib"))
    api(project(":ce:libs:runtime-lib"))
    api(project(":ce:utils:db-schema-ce"))
    api(project(":ce:utils:versions-ce"))

    api(libs.apache.commonsIo)
    api(libs.apache.commonsLang)
    api(libs.bouncyCastle.bcprovJdk18on)
    api(libs.bouncyCastle.bcpkixJdk18on)
    api(libs.flyway.postgres)
    api(libs.hibernate.validation)
    api(libs.jackson.jsr310)
    api(libs.jakarta.el)
    api(libs.jakarta.rsApi)
    api(libs.jakarta.validationApi)
    api(libs.jooq.jooq)

    testAnnotationProcessor(libs.lombok)
    testImplementation(libs.lombok)

    testImplementation(project(":ce:libs:api-clients:java-client"))
    testImplementation(project(":ce:libs:runtime-test-lib"))
    testImplementation(libs.bundles.testUtils)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityCeGroupName.get()

sourceSets {
    main {
        kotlin {
            srcDir(layout.buildDirectory.dir("generated/sources/kotlin"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

val generateCriterionOperatorEnum by tasks.registering(Task::class) {
    // regenerate from the source to ensure we don't miss any criterion operator (at compile time)
    generateEnumerationFromInterfaceFields(
        enumerationPackageName = "de.sovity.edc.ce.utils",
        enumClassName = "CriterionOperatorEnum",
        classToCopy = CriterionOperatorRegistry::class,
    )
}

val kotlinCompile = tasks.withType(KotlinCompile::class) {
    dependsOn(generateCriterionOperatorEnum)
}

fun generateEnumerationFromInterfaceFields(
    enumerationPackageName: String,
    enumClassName: String,
    classToCopy: KClass<CriterionOperatorRegistry>
) {
    val enumClass = ClassName.bestGuess("$enumerationPackageName.$enumClassName")
    val fieldName = "symbol"

    val enumBuilder = TypeSpec.enumBuilder(enumClass)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(fieldName, String::class)
                .build()
        )
        .addProperty(
            PropertySpec.builder(fieldName, String::class, KModifier.PUBLIC)
                .initializer(fieldName)
                .build()
        )
        .addType(
            TypeSpec.companionObjectBuilder()
                .addFunction(
                    FunSpec.builder("parseFrom${fieldName.replaceFirstChar(Char::uppercase)}")
                        .addParameter(fieldName, String::class)
                        .returns(enumClass)
                        .addCode("return entries.first { it.${fieldName} == ${fieldName} }")
                        .build()
                )
                .build()
        )

    classToCopy.java.declaredFields
        .filter { field -> field.name == field.name.uppercase() }
        .forEach { field ->
            enumBuilder.addEnumConstant(
                field.name,
                TypeSpec.anonymousClassBuilder()
                    .addSuperclassConstructorParameter("%S", field.get(null))
                    .build()
            )
        }

    val outputPath = "generated/sources/kotlin"
    val outputDir = project.layout.buildDirectory.dir(outputPath).get().asFile.also { it.mkdirs() }

    val fileSpec = FileSpec.builder(enumerationPackageName, enumClassName)
        .addType(enumBuilder.build())
        .build()

    fileSpec.writeTo(outputDir)
}
