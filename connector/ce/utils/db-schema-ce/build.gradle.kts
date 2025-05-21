/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import nu.studer.gradle.jooq.JooqGenerate
import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Nullability
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq)
}

buildscript {
    dependencies {
        classpath(libs.testcontainers.postgresql)
        classpath(libs.flyway.postgres)
    }
}

val migrationsDir = "src/main/resources/db/migration-ce"
val jooqTargetPackage = "de.sovity.edc.ce.db.jooq"

val flywayMigration = configurations.create("flywayMigration")

dependencies {
    jooqGenerator(libs.postgres)
    flywayMigration(libs.postgres)

    annotationProcessor(libs.lombok)

    api(libs.jooq.jooq)
    api(libs.t9tJooq.jooqPostgresqlJson)

    compileOnly(libs.lombok)
}

var container: JdbcDatabaseContainer<*>? = null

fun jdbcUrl(): String {
    return container?.jdbcUrl ?: error("The test container didn't start!")
}

fun jdbcUser(): String {
    return container?.username ?: error("The test container didn't start!")
}

fun jdbcPassword(): String {
    return container?.password ?: error("The test container didn't start!")
}

fun Task.cacheTask() {
    outputs.cacheIf { true }
    inputs.files(fileTree(migrationsDir))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
}

val startTestcontainer = tasks.register("startTestcontainer") {
    cacheTask()

    doLast {
        val postgresContainer = libs.versions.postgresDbImage.get()
        container = PostgreSQLContainer<Nothing>(postgresContainer)
        container!!.start()
        gradle.buildFinished {
            container?.stop()
        }
    }
}

val jooqTargetDir = "build/generated/jooq/" + jooqTargetPackage.replace(".", "/")


flyway {
    driver = "org.postgresql.Driver"
    schemas = arrayOf("public")

    cleanDisabled = false
    baselineOnMigrate = true
    locations = arrayOf("filesystem:${migrationsDir}")
    configurations = arrayOf("flywayMigration")

    mixed = true
}

tasks.withType<FlywayCleanTask> {
    doFirst {
        require(this is FlywayCleanTask)
        url = jdbcUrl()
        user = jdbcUser()
        password = jdbcPassword()
    }
}

val flywayMigrate = tasks.withType<FlywayMigrateTask> {
    dependsOn.add(startTestcontainer)

    cacheTask()

    doFirst {
        require(this is FlywayMigrateTask)
        url = jdbcUrl()
        user = jdbcUser()
        password = jdbcPassword()
    }
}

val generateDatabaseCode by tasks.registering {
    dependsOn(flywayMigrate)

    group = "sovity"
    description = "Generates the jOOQ database code for the project"
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        excludes = "(.*)flyway_schema_history(.*)"
                        inputSchema = flyway.schemas[0]

                        withForcedTypes(
                            // Force "List<String>" over "String[]" for PostgreSQL "text[]"
                            ForcedType()
                                .withUserType("java.util.List<String>")
                                .withIncludeTypes("_text|_varchar")
                                .withConverter(
                                    """org.jooq.Converter.ofNullable(
                                    String[].class,
                                    (Class<java.util.List<String>>) (Class) java.util.List.class,
                                    array -> array == null ? null : java.util.Arrays.asList(array),
                                    list -> list == null ? null : list.toArray(new String[0])
                                  )"""
                                )
                                .withNullability(Nullability.ALL)
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isRelations = true
                    }
                    target.apply {
                        packageName = jooqTargetPackage
                        directory = jooqTargetDir
                    }
                }
            }
        }
    }
}

tasks.withType<JooqGenerate> {
    cacheTask()
    outputs.dir(project.layout.buildDirectory.dir("generated"))

    dependsOn.add(tasks.withType<FlywayMigrateTask>())
    allInputsDeclared.set(true)

    doFirst {
        require(this is JooqGenerate)

        val jooqConfiguration = JooqGenerate::class.java.getDeclaredField("jooqConfiguration")
            .also { it.isAccessible = true }.get(this) as org.jooq.meta.jaxb.Configuration

        jooqConfiguration.jdbc.apply {
            url = jdbcUrl()
            user = jdbcUser()
            password = jdbcPassword()
        }
    }
    doLast {
        container?.stop()
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

