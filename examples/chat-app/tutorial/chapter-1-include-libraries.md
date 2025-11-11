# Example Use Case App Tutorial - Chat App

## Chapter 1: Setup EDC Utilities

### Connect to the GitHub Maven Registry

The sovity EDC offers client libraries for selected languages, including Java and Kotlin. The used version should always be the version of the used EDC itself to ensure compatibility. These client libraries are hosted in the GitHub Maven Registry of the sovity/edc-ce repository, thus we need to add that maven registry to add the dependency.

> If you haven't set up GitHub Maven Registry credentials yet, please refer to the prerequisites section in the parent [README](../README.md).

```markdown
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
```

### Include sovity EDC Client Libraries

Add the following two libraries to your project dependencies:

- The sovity EDC Java Client, which works with all sovity-built EDC variants, including Catena-compatible and EE variants
- The sovity EDC JSON & JSON-LD Utility library, providing useful constants such as EDC JSON-LD Properties (e.g. Asset ID)

These libraries are essential to interact smoothly with the sovity EDC services.

```markdown
```kotlin
// File:
// backend/build.gradle.kts

// sovity EDC Dependencies
implementation("de.sovity.edc:client:16.2.2")
implementation("de.sovity.edc:jsonld-lib:16.2.2")
```
```
