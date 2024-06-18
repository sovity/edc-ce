import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import nu.studer.gradle.jooq.JooqGenerate

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq)
}

buildscript {
    dependencies {
        classpath(libs.testcontainers.postgresql)
    }
}

val flywayMigration = configurations.create("flywayMigration")

dependencies {
    jooqGenerator(libs.postgres)
    flywayMigration(libs.postgres)

    annotationProcessor(libs.lombok)

    api(libs.jooq.jooq)
    api(libs.t9tJooq.jooqPostgresqlJson)

    compileOnly(libs.lombok)

    implementation(libs.edc.coreSpi)
    implementation(libs.edc.sqlCore)

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation(libs.edc.controlPlaneSql)
    implementation(libs.edc.transactionLocal)
    implementation(libs.tractus.sqlPool)

    implementation(libs.apache.commonsLang)

    implementation(libs.flyway.core)

    implementation(libs.postgres)

    implementation(libs.hikari)

    testImplementation(libs.edc.junit)
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

val startTestcontainer = tasks.register("startTestcontainer") {
    doLast {
        val postgresContainer = libs.versions.postgresDbImage.get()
        container = PostgreSQLContainer<Nothing>(postgresContainer)
        container!!.start()
        gradle.buildFinished {
            container?.stop()
        }
    }
}


val migrationsDir = "../../extensions/postgres-flyway/src/main/resources/db/migration"
val jooqTargetPackage = "de.sovity.edc.ext.db.jooq"

val jooqTargetDir = "build/generated/jooq/" + jooqTargetPackage.replace(".", "/")


flyway {
    driver = "org.postgresql.Driver"
    schemas = arrayOf("public")

    cleanDisabled = false
    cleanOnValidationError = true
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

tasks.withType<FlywayMigrateTask> {
    dependsOn.add(startTestcontainer)
    doFirst {
        require(this is FlywayMigrateTask)
        url = jdbcUrl()
        user = jdbcUser()
        password = jdbcPassword()
    }
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
    dependsOn.add("flywayMigrate")
    inputs.files(fileTree(migrationsDir))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    allInputsDeclared.set(true)
    outputs.cacheIf { true }
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

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
