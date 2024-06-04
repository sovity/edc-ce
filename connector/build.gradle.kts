import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("checkstyle")
    id("maven-publish")
}

dependencies {
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

val downloadArtifact: Configuration by configurations.creating {
    isTransitive = false
}


val identityHubVersion: String by project
val registrationServiceVersion: String by project

// task that downloads the RegSrv CLI and IH CLI
val getJars by tasks.registering(Copy::class) {
    outputs.upToDateWhen { false } //always download

    from(downloadArtifact)
            // strip away the version string
            .rename { s ->
                s.replace("-${identityHubVersion}", "")
                        .replace("-${registrationServiceVersion}", "")
                        .replace("-all", "")
            }
    into(layout.projectDirectory.dir("libs/cli-tools"))
}

// run the download jars task after the "jar" task
tasks {
    jar {
        finalizedBy(getJars)
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    tasks.getByName<Test>("test") {
        val runningOnGithub = System.getenv("GITHUB_CI")?.isNotBlank() ?: false

        useJUnitPlatform {
            if (runningOnGithub) {
                excludeTags = setOf("not-on-github")
            }
        }

        testLogging {
            events = setOf(TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
        }
        failFast = true
    }

    checkstyle {
        toolVersion = "10.9.3"
        configFile = rootProject.file("../docs/dev/checkstyle/checkstyle-config.xml")
        configDirectory.set(rootProject.file("docs/dev/checkstyle"))
        maxErrors = 0 // does not tolerate errors
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            url = uri("https://maven.pkg.github.com/truzzt/mds-ap3")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/ids-basecamp/ids-infomodel-java")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
        maven {
            url = uri("https://pkgs.dev.azure.com/sovity/41799556-91c8-4df6-8ddb-4471d6f15953/_packaging/core-edc/maven/v1")
            name = "AzureRepo"
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")

    val sovityEdcExtensionsVersion: String by project
    version = sovityEdcExtensionsVersion

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/sovity/edc-extensions")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }
}
