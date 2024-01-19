plugins {
    `java-library`
}

dependencies {
    // MDS Related EDC Extensions
    // [...]

    // Currently there are no mds-specific EDC extensions
    // The MDS broker and MDS clearing house extensions are in the process of being migrated to the DSP protocol
    implementation("logging-house:logging-house-client:0.1.0")
}

val sovityEdcGroup: String by project
group = sovityEdcGroup
