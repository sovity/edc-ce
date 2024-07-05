plugins {
    `java-library`
}

dependencies {
    // A minimal EDC that can request catalogs
    api(libs.edc.controlPlaneCore)
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.configurationFilesystem)
    api(libs.edc.controlPlaneAggregateServices)
    api(libs.edc.http)
    api(libs.edc.dsp)
    api(libs.edc.jsonLd)

    // Data Catalog Crawler
    api(project(":extensions:catalog-crawler:catalog-crawler"))
}

group = libs.versions.sovityEdcGroup.get()
