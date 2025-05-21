# Example Use Case App Tutorial - Chat App

## Chapter 1: Setup EDC Utilities

### Connect to the GitHub Maven Registry

The sovity EDC offers client libraries for selected languages, including Java and Kotlin. The used version should always be the version of the used EDC itself to ensure compatibility. These client libraries are hosted in the GitHub Maven Registry of the sovity/edc-ce repository, thus we need to add that maven registry to add the dependency.

```kotlin
// File:
// backend/build.gradle.kts

// Replace:
// additional maven registries...

// With:
maven {
    name = "maven.pkg.github.com/sovity/edc-ce"
    url = uri("https://maven.pkg.github.com/sovity/edc-ce")
    credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_KEY")
    }
}
```

See the prerequisites section of the parent [README](../README.md) for instructions on how to set up the GitHub Maven Registry credentials.

### Include EDC Client Library

We want to add two libraries:

- The sovity EDC Java Client usable for all variants of sovity-built EDCs, including the Catena-compatible variants and EE variants
- The sovity EDC JSON & JSON-LD Utility library, which will contain useful constants, e.g. for EDC JSON-LD Properties such as the Asset ID.

```kotlin
// File:
// backend/build.gradle.kts

// sovity EDC Dependencies
implementation("de.sovity.edc:client:13.0.2")
implementation("de.sovity.edc:jsonld-lib:13.0.2")
```
