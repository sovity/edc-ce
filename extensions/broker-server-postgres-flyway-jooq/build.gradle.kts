import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer

val jooqDbType = "org.jooq.meta.postgres.PostgresDatabase"
val jdbcDriver = "org.postgresql.Driver"
val postgresContainer = "postgres:11-alpine"

val migrationsDir = "src/main/resources/db/migration"
val jooqTargetPackage = "de.sovity.edc.ext.brokerserver.db.jooq"
val jooqTargetSourceRoot = "build/generated/jooq"

val jooqTargetDir = jooqTargetSourceRoot + "/" + jooqTargetPackage.replace(".", "/")
val flywayMigration = configurations.create("flywayMigration")

val edcVersion: String by project
val edcGroup: String by project
val flywayVersion: String by project
val postgresVersion: String by project

buildscript {
    dependencies {
        classpath("org.testcontainers:postgresql:1.18.3")
    }
}

plugins {
    id("org.flywaydb.flyway") version "9.19.1"
    id("nu.studer.jooq") version "7.1.1"
    `java-library`
    `maven-publish`
}

dependencies {
    api("org.jooq:jooq:3.18.4")
    api("com.github.t9t.jooq:jooq-postgresql-json:4.0.0")

    jooqGenerator("org.postgresql:postgresql:42.6.0")
    flywayMigration("org.postgresql:postgresql:42.6.0")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
    compileOnly("org.projectlombok:lombok:1.18.28")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    implementation("${edcGroup}:core-spi:${edcVersion}")
    implementation("${edcGroup}:sql-core:${edcVersion}")

    // Adds Database-Related EDC-Extensions (EDC-SQL-Stores, JDBC-Driver, Pool and Transactions)
    implementation("${edcGroup}:control-plane-sql:${edcVersion}")
    implementation("${edcGroup}:data-plane-instance-store-sql:${edcVersion}")
    implementation("${edcGroup}:sql-pool-apache-commons:${edcVersion}")
    implementation("${edcGroup}:transaction-local:${edcVersion}")
    implementation("org.postgresql:postgresql:${postgresVersion}")

    implementation("org.flywaydb:flyway-core:${flywayVersion}")

    testImplementation("${edcGroup}:junit:${edcVersion}")
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


val sovityEdcExtensionGroup: String by project
group = sovityEdcExtensionGroup

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}