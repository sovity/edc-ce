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
                excludeTags = setOf("exclude-on-github")
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
        configFile = rootProject.file("docs/dev/checkstyle/checkstyle-config.xml")
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
            withGitHubCredentials()
        }
        maven {
            url = uri("https://maven.pkg.github.com/ids-basecamp/ids-infomodel-java")
            withGitHubCredentials()
        }
        maven {
            url =
                uri("https://pkgs.dev.azure.com/sovity/41799556-91c8-4df6-8ddb-4471d6f15953/_packaging/core-edc/maven/v1")
            name = "AzureRepo"
        }
    }
}

subprojects {
    val libs = rootProject.libs

    apply(plugin = "maven-publish")

    version = libs.versions.sovityCeVersion.get()

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/sovity/edc-ce")
                withGitHubCredentials()
            }
        }
    }

    tasks.register("printClasspath") {
        group = libs.versions.sovityEdcGroup.get()
        description = "Prints the classpath so EDC Integration tests can load a runtime of a full EDC including modules in tests"
        println(sourceSets.main.get().runtimeClasspath.asPath)
    }

    java {
        withSourcesJar()
    }
}

fun MavenArtifactRepository.withGitHubCredentials() {
    val gitHubUser = System.getenv("USERNAME")
        ?: project.findProperty("gpr.user") as String?
    val gitHubToken = System.getenv("TOKEN")
        ?: project.findProperty("gpr.key") as String?

    if (gitHubUser.isNullOrBlank() || gitHubToken.isNullOrBlank()) {
        error("Need Gradle Properties 'gpr.user' and 'gpr.key' or environment variables 'USERNAME' and 'TOKEN' with a GitHub PAT to access the GitHub Maven Repository.")
    }

    credentials {
        username = gitHubUser
        password = gitHubToken
    }
}
