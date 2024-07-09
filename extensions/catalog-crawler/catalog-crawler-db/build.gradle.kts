import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer

val jooqDbType = "org.jooq.meta.postgres.PostgresDatabase"
val jdbcDriver = "org.postgresql.Driver"
val postgresContainer = libs.versions.postgresDbImage.get()

val migrationsDir = "src/main/resources/db-crawler/migration"
val jooqTargetPackage = "de.sovity.edc.ext.catalog.crawler.db.jooq"
val jooqTargetSourceRoot = "build/generated/jooq"

val jooqTargetDir = jooqTargetSourceRoot + "/" + jooqTargetPackage.replace(".", "/")
val flywayMigration = configurations.create("flywayMigration")

buildscript {
    dependencies {
        classpath(libs.testcontainers.postgresql)
    }
}

plugins {
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq)
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.jooq.jooq)
    api(libs.t9tJooq.jooqPostgresqlJson)

    jooqGenerator(libs.postgres)
    flywayMigration(libs.postgres)

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation(libs.postgres)
}

sourceSets {
    main {
        java {
            srcDirs.add(File(jooqTargetSourceRoot))
        }
    }
}


val skipTestcontainersEnvVarName = "SKIP_TESTCONTAINERS"
val jdbcUrlEnvVarName = "TEST_POSTGRES_JDBC_URL"
val jdbcUserEnvVarName = "TEST_POSTGRES_JDBC_USER"
val jdbcPasswordEnvVarName = "TEST_POSTGRES_JDBC_PASSWORD"
var container: JdbcDatabaseContainer<*>? = null

fun isTestcontainersEnabled(): Boolean {
    return System.getenv()[skipTestcontainersEnvVarName] != "true"
}

fun jdbcUrl(): String {
    return container?.jdbcUrl
        ?: System.getenv()[jdbcUrlEnvVarName]
        ?: error("Need $jdbcUrlEnvVarName since $skipTestcontainersEnvVarName=true")
}

fun jdbcUser(): String {
    return container?.username
        ?: System.getenv()[jdbcUserEnvVarName]
        ?: error("Need $jdbcUserEnvVarName since $skipTestcontainersEnvVarName=true")
}

fun jdbcPassword(): String {
    return container?.password
        ?: System.getenv()[jdbcPasswordEnvVarName]
        ?: error("Need $jdbcPasswordEnvVarName since $skipTestcontainersEnvVarName=true")
}


tasks.register("startTestcontainer") {
    doLast {
        if (isTestcontainersEnabled()) {
            container = PostgreSQLContainer<Nothing>(postgresContainer)
            container!!.start()
            gradle.buildFinished {
                container?.stop()
            }
        }
    }
}

flyway {
    driver = jdbcDriver
    schemas = arrayOf("public")

    cleanDisabled = false
    cleanOnValidationError = true
    baselineOnMigrate = true
    locations = arrayOf("filesystem:${migrationsDir}")
    configurations = arrayOf("flywayMigration")
    failOnMissingLocations = true

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
    dependsOn.add("startTestcontainer")
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
                        name = jooqDbType
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

tasks.withType<nu.studer.gradle.jooq.JooqGenerate> {
    dependsOn.add("flywayMigrate")
    inputs.files(fileTree(migrationsDir))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    allInputsDeclared.set(true)
    outputs.cacheIf { true }
    doFirst {
        require(this is nu.studer.gradle.jooq.JooqGenerate)

        val jooqConfiguration = nu.studer.gradle.jooq.JooqGenerate::class.java.getDeclaredField("jooqConfiguration")
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

group = libs.versions.sovityCatalogCrawlerGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
